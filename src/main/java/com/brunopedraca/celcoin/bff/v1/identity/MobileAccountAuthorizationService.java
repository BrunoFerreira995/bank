package com.brunopedraca.celcoin.bff.v1.identity;

import org.springframework.stereotype.Service;

@Service
public class MobileAccountAuthorizationService {
    private final MobileUserAccountRepository accounts;
    private final MobileUserRoleRepository roles;
    private final MobileAccountGrantRepository grants;
    private final MobileUserRepository users;
    public MobileAccountAuthorizationService(MobileUserAccountRepository accounts, MobileUserRoleRepository roles, MobileAccountGrantRepository grants, MobileUserRepository users) {
        this.accounts = accounts; this.roles = roles; this.grants = grants; this.users = users;
    }
    public void requireRead(String accountId) { require(accountId, AccountPermission.READ); }
    public void requireWrite(String accountId) { require(accountId, AccountPermission.WRITE); }
    public void requireRisk(String accountId) { MobileAuthentication.requireStepUp(); require(accountId, AccountPermission.RISK); }
    public java.util.List<String> ownedAccountIds() {
        java.util.UUID userId = MobileAuthentication.requiredUserId();
        return accounts.findByUserId(userId).stream().map(MobileUserAccount::accountId).toList();
    }
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String activeAccountId() {
        return users.findById(MobileAuthentication.requiredUserId()).map(MobileUser::activeAccountId).orElse(null);
    }
    @org.springframework.transaction.annotation.Transactional
    public void selectActiveAccount(String accountId) {
        requireRead(accountId);
        users.findById(MobileAuthentication.requiredUserId()).orElseThrow(MobileUnauthorizedException::new).setActiveAccountId(accountId);
    }
    private void require(String accountId, AccountPermission permission) {
        java.util.UUID userId = MobileAuthentication.requiredUserId();
        boolean owns = accounts.existsByUserIdAndAccountId(userId, accountId);
        boolean granted = grants.existsByUserIdAndAccountIdAndPermission(userId, accountId, permission);
        if (!AccountAuthorizationPolicy.permits(java.util.Set.copyOf(roles.findRolesByUserId(userId)), owns, granted, permission)) throw new MobileForbiddenException();
    }
}
