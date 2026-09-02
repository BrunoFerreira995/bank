package com.brunopedraca.celcoin.bff.v1.identity;

import java.util.Set;

/** RBAC decides eligible actions; ABAC requires the evaluated account attribute to match a grant. */
final class AccountAuthorizationPolicy {
    private AccountAuthorizationPolicy() {}
    static boolean permits(Set<MobileRole> roles, boolean ownsAccount, boolean hasExplicitGrant, AccountPermission permission) {
        if (roles.contains(MobileRole.CUSTOMER) && ownsAccount) return true;
        if (roles.contains(MobileRole.ADMIN)) return hasExplicitGrant;
        if (roles.contains(MobileRole.OPERATIONS)) return hasExplicitGrant && permission != AccountPermission.RISK;
        return roles.contains(MobileRole.SUPPORT) && hasExplicitGrant && permission == AccountPermission.READ;
    }
}
