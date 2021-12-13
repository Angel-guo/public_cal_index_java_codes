package com.catl.cal_index.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.Future;

@Data
@AllArgsConstructor
public class CalIndexStatusFutures extends StatusFutures{
    private String dateTime; // 程序运行时的时间戳

    public CalIndexStatusFutures(Integer status, List<Future<List<String>>> futures, Integer id, String fileName, String dateTime) {
        super(status, futures, id, fileName);
        this.dateTime = dateTime;
    }
}
