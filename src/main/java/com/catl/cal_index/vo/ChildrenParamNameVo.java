package com.catl.cal_index.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildrenParamNameVo {
    @ApiModelProperty(value = "子参数 --- 名字label不要更改， 前端会用到")
    private String label;

    @ApiModelProperty(value = "子参数名称 ---名字  不要更改， 前段会用到")
    private String value;

    @ApiModelProperty(value = "参数类型")
    private String paramType;
}
