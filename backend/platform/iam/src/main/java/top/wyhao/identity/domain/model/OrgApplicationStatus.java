package top.wyhao.identity.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrgApplicationStatus {
    PENDING(0),
    APPROVED(1),
    REJECTED(2),
    CANCELLED(3);

    @JsonValue
    @EnumValue
    private final int value;
}
