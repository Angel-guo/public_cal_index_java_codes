package com.catl.cal_index.manage.calIndex;

import com.alibaba.fastjson.JSONObject;
import com.catl.cal_index.entity.CalIndexStatusFutures;
import com.catl.cal_index.entity.calIndex.CalIndexRecord;
import com.catl.cal_index.handler.calIndex.CalIndexCallableTask;
import com.catl.cal_index.handler.calIndex.CalIndexMapHandlerCallable;
import com.catl.cal_index.mapper.calIndex.CalIndexParamMapper;
import com.catl.cal_index.mapper.calIndex.CalIndexRecordMapper;
import com.catl.cal_index.mapper.calIndex.CalIndexTypeMapper;
import com.catl.cal_index.request.ParamNameReq;
import com.catl.cal_index.service.impl.CalIndexServiceImpl;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Data
@Component
public class CalIndexManage {

    private CalIndexManage() {
    }

    // controller 接收的前端参数
    private List<ParamNameReq> params;
    private CalIndexRecordMapper calIndexRecordMapper;
    private CalIndexParamMapper calIndexParamMapper;
    private CalIndexTypeMapper calIndexTypeMapper;

    // 本地文件存储路径
    public String filePath = "/Users/4paradigm/test/cal_index";
    // hdfs 文件存储路径
    public String hdfsPath = "/Users/4paradigm/test/cal_index";

    private static final Logger logger = LoggerFactory.getLogger(CalIndexManage.class);
    private static final CalIndexManage calIndexManage = new CalIndexManage();
    private volatile ArrayBlockingQueue<Map<String, String>> queue = new ArrayBlockingQueue<>(80);
    private volatile ConcurrentHashMap<String, CalIndexStatusFutures> map = new ConcurrentHashMap<>();
    private ThreadPoolExecutor manageThreadPool = new ThreadPoolExecutor(5, 13, 200, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(5));
    private ThreadPoolExecutor executeThreadPool = new ThreadPoolExecutor(5, 13, 200, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(5));
    // CalIndexManage 单例
    public static CalIndexManage getManage() {
        return calIndexManage;
    }

    // 初始化 CalIndexManage
    public void initMapper(CalIndexRecordMapper calIndexRecordMapper,
                           CalIndexParamMapper calIndexParamMapper, CalIndexTypeMapper calIndexTypeMapper) {
        CalIndexManage manage = CalIndexManage.getManage();
        manage.calIndexRecordMapper = calIndexRecordMapper;
        manage.calIndexParamMapper = calIndexParamMapper;
        manage.calIndexTypeMapper = calIndexTypeMapper;
    }

    /**
     * 在项目初始化后才初始化，，DemoApplicationRunner中有进行初始化，监听 CalIndexManage中的 Queue 和 Map
     */
    public void initCalIndexTask() {
        Callable<Object> getQueueCallable = new Callable() {
            @Override
            public Object call() throws Exception {
                while (true) {
                    Queue<Map<String, String>> queue = CalIndexManage.getManage().getQueue();
                    // ########################################################################
                    // ########################      map 维护策略
                    // ########################################################################
                    // 监控map中的任务数量, 运行状态： -1 初始态；0 运行中；1 运行成功；2 运行失败
                    // 将非运行态的移除
                    if (map.size() == 1000) {
                        map.entrySet().removeIf(entry -> entry.getValue().getStatus() == 1 || entry.getValue().getStatus() == 2);
                    }
                    // 将queue中的任务，取出挨个执行
                    if (queue.size() > 0 && map.size() <= 1000) { // map.size()<1000 , 控制 map 中最多只有1000个任务在执行
                        Map<String, String> poll = queue.poll();
                        for (String key : poll.keySet()) {
                            CalIndexManage.getManage().execute(key, poll.get(key));
                        }
                    } else {
                        Thread.sleep(100);
                    }
                }
            }
        };

        // 将任务放到 manage中管理
        CalIndexManage manage = CalIndexManage.getManage();
        manage.getManageThreadPool().submit(getQueueCallable);
        manage.getManageThreadPool().submit(new CalIndexMapHandlerCallable(manage.filePath, manage.hdfsPath, manage.calIndexRecordMapper));
    }

    /**
     * 判断任务是否已存在，若存在返回false，若不存在就加入任务执行队列
     *
     * @param calIndex      //     * @param calIndexVarsDir = 指标计算类型+指标计算参数(用下划线分割)
     * @param calVarsString = 指标计算参数(逗号分隔)
     * @param datetime
     * @param params        接收的参数，后面执行执行脚本时会用到
     * @return
     */
    public synchronized boolean submit(String calIndex, String calVarsString, String datetime, List<ParamNameReq> params) {
        calIndexManage.params = params;  // 放到manage中，后面执行脚本会用到
        CalIndexManage manage = CalIndexManage.getManage();
        ConcurrentHashMap<String, CalIndexStatusFutures> map = manage.getMap();
        // 如果任务已经存在，就返回false，不重复执行
        if (map.containsKey(calIndex + "_" + calVarsString)) {return false;}
        HashMap<String, String> temp_map = new HashMap<>();
        // 将CalIndexManage中map内的任务，放到执行任务队列中
        temp_map.put(calIndex + "_" + calVarsString, calVarsString.concat("_") + datetime);
        return manage.getQueue().offer(temp_map);
    }

    /**
     * queue中监听到有任务就调用此方法执行任务
     * <p>
     * //     * @param calIndexVarsDir
     * //     * @param calIndex_dateTime
     */
    public synchronized void execute(String calIndex_varsString, String calVarsString_dataTime) {
        logger.info("CalIndexManage execute method accpeted parameters： calIndex_varsString:{}, calVarsString_dataTime{}",
                calIndex_varsString, calVarsString_dataTime);
        CalIndexManage manage = CalIndexManage.getManage();

        // 将 calIndex, dateTime, varString 从参数中切割出来
        String[] vars = calVarsString_dataTime.split("_");
        String calVarsString = vars[0];
        String dateTime = vars[vars.length - 1];
        String calIndex = calIndex_varsString.split("_")[0];

        // map中初始化任务，状态置为 -1
        map.put(calIndex_varsString, new CalIndexStatusFutures(-1, null, null, null, null));
        CalIndexRecord calIndexRecord = new CalIndexRecord(calIndex, calVarsString, null, -1);
        logger.info("CalIndexManage map content: {}", JSONObject.toJSON(map));

        // 插入一条查询记录
        calIndexRecordMapper.insert(calIndexRecord);
        // 执行脚本并将结果添加到 FutureTask 中了
        ArrayList<Future<List<String>>> futures = new ArrayList<>();
        CalIndexCallableTask calIndexCallableTask = new CalIndexCallableTask(calIndex,
                calVarsString, manage.params, calIndexRecordMapper, calIndexParamMapper, calIndexTypeMapper,
                hdfsPath + "/" + calIndex_varsString + "_" + dateTime, hdfsPath, filePath, calIndexRecord);
        futures.add(manage.getExecuteThreadPool().submit(calIndexCallableTask));

        // 更新 map 中的数据
        CalIndexStatusFutures calIndexStatusFutures = map.get(calIndex_varsString);
        calIndexStatusFutures.setFutures(futures);
        calIndexStatusFutures.setId(calIndexRecord.getId());
        calIndexStatusFutures.setFileName(calIndex + "_" + dateTime);
        calIndexStatusFutures.setStatus(0);
        calIndexStatusFutures.setDateTime(dateTime);
        map.put(calIndex_varsString, calIndexStatusFutures);

        logger.info("after update CalIndexManage map content: {}", JSONObject.toJSON(map));
    }

}
