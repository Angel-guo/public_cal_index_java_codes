package com.catl.cal_index.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentChartVo {
    private String vin;  // vin
    private List<List<Object>> dataPoint = new ArrayList<>(); // 散点图，折线图，柱状图 x,y轴  [x,y] 形如：[[vin1,2],[vin2,3],[vin3,4]]
    private double percent; // 饼图 百分比
    private String carType; // 私家车 PRIVATE_CAR  运营车 OPERATING_CAR
}
