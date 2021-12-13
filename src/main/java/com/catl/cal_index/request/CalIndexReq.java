package com.catl.cal_index.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalIndexReq {

    @ApiModelProperty(value = "计算类型")
    @JsonProperty(value = "calIndex")
    private String calIndex;

    @ApiModelProperty(value = "计算参数")
    @JsonProperty(value = "params")
    private List<ParamNameReq> params;

    @ApiModelProperty(value = "是否强制执行计算 0 强制，1 非强制")
    @JsonProperty(value = "downloadStats")
    private Integer downloadStats = 0;

}
