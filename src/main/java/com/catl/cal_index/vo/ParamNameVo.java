package com.catl.cal_index.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamNameVo {

    public ParamNameVo(Integer id, Integer pid, String param, String paramName, String paramType) {
        this.id = id;
        this.pid = pid;
        this.param = param;
        this.paramName = paramName;
        this.paramType = paramType;
    }

    //    定义主键名和自增方式
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "指标计算父参数，为-1表示为一级参数，>=0 表示其他参数的子参数")
    private Integer pid;

    @ApiModelProperty(value = "指标具体计算的唯一标示")
    private String calIndex;

    @ApiModelProperty(value = "参数")
    private String param;

    @ApiModelProperty(value = "参数名称")
    private String paramName;

    @ApiModelProperty(value = "参数类型")
    private String paramType;

    @ApiModelProperty(value = "子参数类型(例如下拉框这种[日、周、月、季度]等)---- options 名字不能改，前端要判断")
    private List<ChildrenParamNameVo> options;

}
