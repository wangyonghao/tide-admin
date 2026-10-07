package top.wyhao.identity.domain.service;

import org.junit.jupiter.api.Test;
import top.wyhao.identity.domain.model.MembershipStatus;
import top.wyhao.identity.domain.model.SysMembership;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MembershipRules} 有效成员判定。
 */
class MembershipRulesTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 10, 6, 12, 0);

    @Test
    void effectiveWhenNormalAndNotExpired() {
        SysMembership membership = active();
        assertThat(MembershipRules.isEffective(membership, now)).isTrue();
    }

    @Test
    void ineffectiveWhenDeleted() {
        SysMembership membership = active();
        membership.setDeleted(1);
        assertThat(MembershipRules.isEffective(membership, now)).isFalse();
    }

    @Test
    void ineffectiveWhenDisabledOrInvited() {
        SysMembership disabled = active();
        disabled.setStatus(MembershipStatus.DISABLED.getValue());
        assertThat(MembershipRules.isEffective(disabled, now)).isFalse();

        SysMembership invited = active();
        invited.setStatus(MembershipStatus.INVITED.getValue());
        assertThat(MembershipRules.isEffective(invited, now)).isFalse();
    }

    @Test
    void ineffectiveWhenExpired() {
        SysMembership membership = active();
        membership.setExpiredAt(now.minusSeconds(1));
        assertThat(MembershipRules.isEffective(membership, now)).isFalse();
    }

    @Test
    void effectiveWhenExpiredAtInFuture() {
        SysMembership membership = active();
        membership.setExpiredAt(now.plusDays(1));
        assertThat(MembershipRules.isEffective(membership, now)).isTrue();
    }

    @Test
    void ineffectiveWhenNull() {
        assertThat(MembershipRules.isEffective(null, now)).isFalse();
    }

    private static SysMembership active() {
        SysMembership membership = new SysMembership();
        membership.setDeleted(0);
        membership.setStatus(MembershipStatus.NORMAL.getValue());
        membership.setExpiredAt(null);
        return membership;
    }
}
