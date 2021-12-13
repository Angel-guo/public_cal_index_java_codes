package com.catl.cal_index.entity.calIndex;


import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
//定义表名 - 指标计算类型表
@TableName(value = "cal_index_type")
public class CalIndexType {

    //    定义主键名和自增方式
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    //    非主键的字段
    @TableField(value = "cal_type")
    @ApiModelProperty(value = "指标计算大类型-(例如:充电阶段-温度)")
    private String calType;
    @TableField(value = "cal_type_name")
    @ApiModelProperty(value = "指标计算大类型名称-(例如:充电阶段-温度)")
    private String calTypeName;

    @TableField(value = "index_type")
    @ApiModelProperty(value = "指标计算大类型下的-具体类型(充电阶段-温度-最高温度-每10秒一次)")
    private String indexType;

    @TableField(value = "index_type_name")
    @ApiModelProperty(value = "指标计算大类型下的-具体类型(充电阶段-温度-最高温度-每10秒一次)")
    private String indexTypeName;

    @TableField(value = "cal_index")
    @ApiModelProperty(value = "指标具体计算的唯一标示")
    private String calIndex;

    @TableField(value = "result_type")
    @ApiModelProperty(value = "计算结果要展示的类型 Scatter 散点图，Pie 饼图；Line 折线图，Bar 柱状图")
    private String resultType;

    @TableField(value = "cal_number_type")
    @ApiModelProperty(value = "计算结果内容 单车 SINGLE  多车 MULTI")
    private String carNumberType;

    @TableField(value = "x_column")
    @ApiModelProperty(value = "散点图、柱状图、折线图等的 x轴 取计算后的文件的哪一列")
    private String xColumn;

    @TableField(value = "y_column")
    @ApiModelProperty(value = "散点图、柱状图、折线图等的 y轴 取计算后的文件的哪一列")
    private String yColumn;

    @TableField(value = "pie_column")
    @ApiModelProperty(value = "饼状图百分比，取计算后文件的哪一列数据")
    private String pieColumn;

    @TableField(value = "py_store_path")
    @ApiModelProperty(value = "指标计算文件-.py文件存储位置")
    private String pyStorePath;

    @TableField(value = "run_store_path")
    @ApiModelProperty(value = "指标计算文件-runcode.sh存储位置")
    private String runStorePath;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

}
