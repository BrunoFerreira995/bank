package com.brunopedraca.celcoin.bff.v1.identity;

import com.brunopedraca.celcoin.bff.MobileBffProperties;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MobileSessionService {
    private final MobileUserRepository users;
    private final MobileSessionRepository sessions;
    private final MobileMfaChallengeRepository challenges;
    private final MobileBffProperties properties;
    private final MfaSecretCipher mfaSecretCipher;

    public MobileSessionService(MobileUserRepository users, MobileSessionRepository sessions, MobileMfaChallengeRepository challenges, MobileBffProperties properties, MfaSecretCipher mfaSecretCipher) {
        this.users = users;
        this.sessions = sessions;
        this.challenges = challenges;
        this.properties = properties;
        this.mfaSecretCipher = mfaSecretCipher;
    }

    @Transactional
    public SessionResult authenticate(String login, String password) {
        MobileUser user = users.findByLogin(normalize(login)).orElse(null);
        boolean valid = user != null && user.active() && PasswordHasher.matches(password, user.passwordHash());
        if (user == null) PasswordHasher.dummyMatches(password);
        if (!valid) throw new MobileUnauthorizedException();
        if (user.mfaSecret() != null && !user.mfaSecret().isBlank()) {
            MobileMfaChallenge challenge = challenges.save(new MobileMfaChallenge(user.id(), OffsetDateTime.now().plus(properties.mfaChallengeTtl())));
            return SessionResult.challenge(challenge.id().toString());
        }
        return issue(user.id(), false);
    }

    @Transactional(noRollbackFor = MobileUnauthorizedException.class)
    public SessionResult verifyMfa(UUID challengeId, String code) {
        MobileMfaChallenge challenge = challenges.findById(challengeId).filter(value -> value.usable(OffsetDateTime.now())).orElseThrow(MobileUnauthorizedException::new);
        MobileUser user = users.findById(challenge.userId()).filter(MobileUser::active).orElseThrow(MobileUnauthorizedException::new);
        if (!TotpVerifier.matches(mfaSecretCipher.decrypt(user.mfaSecret()), code)) {
            challenge.failedAttempt();
            throw new MobileUnauthorizedException();
        }
        challenge.consume();
        return issue(user.id(), true);
    }

    @Transactional
    public SessionResult refresh(String refreshToken) {
        MobileSession previous = sessions.findByRefreshTokenHash(TokenHasher.hash(refreshToken))
                .filter(value -> value.refreshActive(OffsetDateTime.now()))
                .orElseThrow(MobileUnauthorizedException::new);
        previous.revoke();
        return issue(previous.userId(), false);
    }

    @Transactional
    public SessionAuthentication authenticateAccessToken(String accessToken) {
        return sessions.findByAccessTokenHash(TokenHasher.hash(accessToken))
                .filter(value -> value.accessActive(OffsetDateTime.now()))
                .map(value -> new SessionAuthentication(value.userId(), value.stepUpActive(OffsetDateTime.now())))
                .orElseThrow(MobileUnauthorizedException::new);
    }

    @Transactional
    public void revokeCurrentSession(String accessToken) {
        sessions.findByAccessTokenHash(TokenHasher.hash(accessToken)).ifPresent(MobileSession::revoke);
    }

    @Transactional
    public void stepUp(String accessToken, String code) {
        MobileSession session = sessions.findByAccessTokenHash(TokenHasher.hash(accessToken)).filter(value -> value.accessActive(OffsetDateTime.now())).orElseThrow(MobileUnauthorizedException::new);
        MobileUser user = users.findById(session.userId()).filter(MobileUser::active).orElseThrow(MobileUnauthorizedException::new);
        if (user.mfaSecret() == null || !TotpVerifier.matches(mfaSecretCipher.decrypt(user.mfaSecret()), code)) throw new MobileUnauthorizedException();
        session.markStepUp();
    }

    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        MobileUser user = users.findById(MobileAuthentication.requiredUserId()).filter(MobileUser::active)
                .orElseThrow(MobileUnauthorizedException::new);
        if (!PasswordHasher.matches(currentPassword, user.passwordHash())) throw new MobileUnauthorizedException();
        user.changePassword(PasswordHasher.hash(newPassword));
    }

    @Transactional(readOnly = true)
    public Profile profile() {
        MobileUser user = users.findById(MobileAuthentication.requiredUserId()).orElseThrow(MobileUnauthorizedException::new);
        return new Profile(user.login());
    }

    private SessionResult issue(UUID userId, boolean mfaVerified) {
        String accessToken = TokenHasher.newToken();
        String refreshToken = TokenHasher.newToken();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plus(properties.sessionTtl());
        sessions.save(MobileSession.create(userId, TokenHasher.hash(accessToken), TokenHasher.hash(refreshToken), expiresAt, now.plus(properties.refreshTtl()), mfaVerified));
        return SessionResult.authenticated(accessToken, refreshToken, expiresAt);
    }

    private static String normalize(String login) { return login.trim().toLowerCase(Locale.ROOT); }

    public record SessionResult(String status, String accessToken, String refreshToken, OffsetDateTime expiresAt, String challengeId) {
        static SessionResult authenticated(String accessToken, String refreshToken, OffsetDateTime expiresAt) { return new SessionResult("AUTHENTICATED", accessToken, refreshToken, expiresAt, null); }
        static SessionResult challenge(String challengeId) { return new SessionResult("MFA_REQUIRED", null, null, null, challengeId); }
    }
    public record Profile(String login) {}
    public record SessionAuthentication(UUID userId, boolean stepUpAuthenticated) {}
}
