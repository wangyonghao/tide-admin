package top.wyhao.identity.domain.service;

import org.junit.jupiter.api.Test;
import top.wyhao.identity.domain.model.MembershipScopeType;
import top.wyhao.identity.domain.model.MembershipStatus;
import top.wyhao.identity.domain.model.SysMembership;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MembershipTenantRulesTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 10, 17, 12, 0);

    @Test
    void primaryTenantMembershipIsEffectiveWhenNormal() {
        SysMembership membership = SysMembership.primaryTenant(1L, 9L);
        assertThat(membership.getScopeType()).isEqualTo(MembershipScopeType.TENANT);
        assertThat(membership.getIsPrimary()).isTrue();
        assertThat(MembershipRules.isEffective(membership, now)).isTrue();
    }

    @Test
    void invitedTenantMembershipNotEffective() {
        SysMembership membership = SysMembership.tenant(1L, 9L);
        membership.setStatus(MembershipStatus.INVITED.getValue());
        assertThat(MembershipRules.isEffective(membership, now)).isFalse();
    }
}
