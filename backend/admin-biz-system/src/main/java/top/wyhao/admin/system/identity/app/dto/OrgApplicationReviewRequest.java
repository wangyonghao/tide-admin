package top.wyhao.admin.system.identity.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrgApplicationReviewRequest {
    @NotNull
    private Boolean approve;
}
