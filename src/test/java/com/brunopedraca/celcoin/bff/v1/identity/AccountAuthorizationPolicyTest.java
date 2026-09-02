package com.brunopedraca.celcoin.bff.v1.identity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;

class AccountAuthorizationPolicyTest {
    @Test
    void customerCannotReadAnotherCustomersAccount() {
        assertThat(AccountAuthorizationPolicy.permits(Set.of(MobileRole.CUSTOMER), false, false, AccountPermission.READ)).isFalse();
    }

    @Test
    void supportCannotWriteEvenWhenItHasAnAccountGrant() {
        assertThat(AccountAuthorizationPolicy.permits(Set.of(MobileRole.SUPPORT), false, true, AccountPermission.WRITE)).isFalse();
    }

    @Test
    void administratorStillNeedsAnExplicitAccountGrant() {
        assertThat(AccountAuthorizationPolicy.permits(Set.of(MobileRole.ADMIN), false, false, AccountPermission.READ)).isFalse();
        assertThat(AccountAuthorizationPolicy.permits(Set.of(MobileRole.ADMIN), false, true, AccountPermission.RISK)).isTrue();
    }
}
