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
public class CalIndexTypeSubVo {
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "指标计算大类型-(例如:充电阶段-温度)")
    private String calType;
    private String calTypeName;

    @ApiModelProperty(value = "指标计算大类型下的-具体类型(充电阶段-温度-最高温度-每10秒一次)")
    private String indexType;

    @ApiModelProperty(value = "指标计算大类型下的-具体类型名称(充电阶段-温度-最高温度-每10秒一次)")
    private String indexTypeName;

    @ApiModelProperty(value = "指标具体计算的唯一标示")
    private String calIndex;

    @ApiModelProperty(value = "具体指标的计算参数")
    private List<ParamNameVo> paramNameVos;

}
