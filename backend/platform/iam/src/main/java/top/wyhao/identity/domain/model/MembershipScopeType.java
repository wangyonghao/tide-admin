package top.wyhao.identity.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 成员关系范围类型。
 */
@Getter
@RequiredArgsConstructor
public enum MembershipScopeType {

    DEPARTMENT("DEPARTMENT"),
    ROLE("ROLE"),
    TENANT("TENANT");

    @JsonValue
    @EnumValue
    private final String value;
}
