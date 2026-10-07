package top.wyhao.identity.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 成员关系状态。
 */
@Getter
@RequiredArgsConstructor
public enum MembershipStatus {

    DISABLED(0),
    NORMAL(1),
    INVITED(2);

    @JsonValue
    @EnumValue
    private final int value;
}
