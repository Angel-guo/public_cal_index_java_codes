package com.catl.cal_index.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  CalIndexResultVo {

    @ApiModelProperty(value = "计算结果数量  resultNum如果是-1，表示没有查询结果，小于50展示图表，大于50直接下载")
    private Integer resultNum;

    @ApiModelProperty(value = "计算结果内容")
    private List<ContentChartVo> resultContentsList; // 多车类型数据

    @ApiModelProperty(value = "计算结果内容 单车 SINGLE  多车 MULTI")
    private String carNumberType;

    @ApiModelProperty(value = "计算结果要展示的类型 Scatter 散点图，Pie 饼图；Line 折线图，Bar 柱状图")
    private String resultType;

}
