package com.catl.cal_index.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentChartVo {
    private List<List<String>> dataPoint = new ArrayList<>(); // 散点图，折线图，柱状图 x,y轴  [x,y] 形如：[[vin1,2],[vin2,3],[vin3,4]]
    private String carType; // 私家车 PRIVATE_CAR  运营车 OPERATING_CAR
    private Integer dataNum; // 图标展示的数据量
}
