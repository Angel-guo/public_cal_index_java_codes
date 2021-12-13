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
//定义表名 --  指标计算不定参数
@TableName(value = "cal_index_param")
public class CalIndexParam{

    //    定义主键名和自增方式
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "pid")
    @ApiModelProperty(value = "指标计算父参数，为-1表示为一级参数，>=0 表示其他参数的子参数")
    private Integer pid;

    @TableField(value = "cal_index")
    @ApiModelProperty(value = "指标具体计算的唯一标示")
    private String calIndex;

    @TableField(value = "param")
    @ApiModelProperty(value = "指标计算参数")
    private String param;

    @TableField(value = "param_name")
    @ApiModelProperty(value = "指标计算参数名称")
    private String paramName;

    @TableField(value = "param_type")
    @ApiModelProperty(value = "指标计算参数类型(TIME 比如开始时间、结束时间，STRING 比如车企, LIST， 比如多个vin，一个vin也用过List类型)")
    private String paramType;

    @TableField(value = "param_formal")
    @ApiModelProperty(value = "指标计算参数传入 runcode.sh 中时用到的是 -v,-p,-s,还是 -d 等")
//    指标计算参数传入 runcode.sh 中时用到的是 -v,-p,-s,还是 -d 等
//    -p 代表 pyspark文件，-n 代表 file_name 文件名 , -d 代表 path 存储路径，
//    -v 代表 vin vin号，-s 代表 start_time 开始时间,  -e 代表end_time 结束时间,
//    -a 代表 automobile_enteprise 车企 ，
//    -t 代表 time_devision 时间分割段[日、周、月、季度等]
    private String paramFormal;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

}
