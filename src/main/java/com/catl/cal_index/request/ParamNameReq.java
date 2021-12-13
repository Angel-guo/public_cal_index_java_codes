package com.catl.cal_index.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ParamNameReq {

    @ApiModelProperty(value = "参数")
    private String param;

    @ApiModelProperty(value = "参数值")
    private String paramValue;

    @ApiModelProperty(value = "参数类型")
    private String paramType;

}
