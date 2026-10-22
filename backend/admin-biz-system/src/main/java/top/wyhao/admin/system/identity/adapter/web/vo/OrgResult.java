package top.wyhao.admin.system.identity.adapter.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "组织简要信息")
public class OrgResult {
    private Long id;
    private String name;
    private String code;
    private Boolean primary;
}
