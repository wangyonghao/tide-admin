package top.wyhao.admin.system.identity.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrgInviteStatus {
    PENDING(0),
    ACCEPTED(1),
    REJECTED(2),
    EXPIRED(3),
    REVOKED(4);

    @JsonValue
    @EnumValue
    private final int value;
}
