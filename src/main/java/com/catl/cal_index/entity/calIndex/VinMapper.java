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
// shm 148 need vin or mvin
@TableName(value = "vin_mapper")
public class VinMapper {

    @TableField(value = "vin")
    @ApiModelProperty(value = "明文 vin")
    private String vin;

    @TableField(value = "mvin")
    @ApiModelProperty(value = "密文 vin")
    private String mvin;

}
