package com.catl.cal_index.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalIndexTypeVo {

    public CalIndexTypeVo(String calType, String calTypeName) {
        this.calType = calType;
        this.calTypeName = calTypeName;
    }

    @ApiModelProperty(value = "指标计算大类型-(例如:充电阶段-温度)")
    private String calType;
    private String calTypeName;

    @ApiModelProperty(value = "指标计算大类型下-具体指标")
    List<CalIndexTypeSubVo> calIndexTypeSubVos;

}
