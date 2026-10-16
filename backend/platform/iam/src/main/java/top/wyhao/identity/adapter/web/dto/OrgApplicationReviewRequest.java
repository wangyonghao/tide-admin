package top.wyhao.identity.adapter.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrgApplicationReviewRequest {
    @NotNull
    private Boolean approve;
}
