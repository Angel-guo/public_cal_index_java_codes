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
@TableName(value = "car_data_message")
public class CarBatteryMap {

    //    定义主键名和自增方式
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "automobile_enterprise")
    @ApiModelProperty(value = "汽车企业")
    private String car;

    @TableField(value = "battery_model")
    @ApiModelProperty(value = "电池型号")
    private String cap;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
}
