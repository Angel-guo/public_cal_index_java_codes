package com.catl.cal_index.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.Future;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusFutures {
    public Integer status; // 程序运行状态， status -1 初始未运行状态， 0 运行中， 1 运行成功
    public List<Future<List<String>>> futures; // 异步线程任务
    public Integer id; // 程序运行记录id
    public String fileName; // 指标查询后，保存的文件名
}
