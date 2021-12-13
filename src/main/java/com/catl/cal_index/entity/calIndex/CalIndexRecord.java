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
//定义表名
@TableName(value = "cal_index_record")
public class CalIndexRecord {
    public CalIndexRecord(String calIndex, String calVars, String result, Integer executeStats) {
        this.calIndex = calIndex;
        this.calVars = calVars;
        this.result = result;
        this.status = executeStats;
    }

    public CalIndexRecord(Integer id, Integer status) {
        this.id = id;
        this.status = status;
    }

    //    定义主键名和自增方式
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    //    非主键的字段
    @TableField(value = "cal_index")
    @ApiModelProperty(value = "计算类型")
    private String calIndex;

    @TableField(value = "cal_vars")
    @ApiModelProperty(value = "计算参数")
    private String calVars;

    @TableField(value = "result")
    @ApiModelProperty(value = "计算结果(文件地址)")
    private String result;

    @TableField(value = "status")
    @ApiModelProperty(value = "运行状态 -1:刚添加任务, 0:运行中, 1:成功, 2:运行失败")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
