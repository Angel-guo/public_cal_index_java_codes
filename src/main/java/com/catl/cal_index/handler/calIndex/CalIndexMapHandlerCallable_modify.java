package com.catl.cal_index.handler.calIndex;

import com.catl.cal_index.entity.CalIndexStatusFutures;
import com.catl.cal_index.entity.StatusFutures;
import com.catl.cal_index.entity.calIndex.CalIndexRecord;
import com.catl.cal_index.manage.calIndex.CalIndexManage;
import com.catl.cal_index.mapper.calIndex.CalIndexRecordMapper;
import com.catl.cal_index.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Data
@AllArgsConstructor
public class CalIndexMapHandlerCallable_modify implements Callable {
    private static final Logger logger = LoggerFactory.getLogger(Callable.class);

    private String filePath;
    private String hdfsPath;
    private CalIndexRecordMapper calIndexRecordMapper;

    @Override
    public Object call() throws Exception {
        while (true) {
            ConcurrentHashMap<String, CalIndexStatusFutures> map = CalIndexManage.getManage().getMap();
            if (map.size() > 0) {
                for (String s : map.keySet()) {
                    if (map.get(s).getStatus() != -1) {
                        // 下面有地方用到这里的参数，所以这里初始化一下
                        String fileName = "";
                        int returnStatus = 0;

                        StatusFutures statusFutures = map.get(s);
                        for (Future<List<String>> future : statusFutures.getFutures()) {
                            while (!future.isDone()) {
                                Thread.sleep(100);
                            }
                            fileName = future.get().get(0);
                            System.out.println("CalIndexMapHandlerCallable----call --------------" + fileName);
                            System.out.println("CalIndexMapHandlerCallable ----- filename：" + fileName);
                            returnStatus = Integer.parseInt(future.get().get(1));

                            if (returnStatus != 1) {
                                future.cancel(true);
                            }
                        }

                        // 指标计算这里，应该只有一个文件落地成功就好
                        if (returnStatus != 0) {
                            logger.error("task execute exception...");
//                            this.calIndexTaskMessageMapper.updateById(new CalIndexTaskMessage(statusFutures.getId(), 2));
                            this.calIndexRecordMapper.updateById(new CalIndexRecord(statusFutures.getId(), 2));
                            map.remove(s);
                        }
                        //成功就创建ok文件，并修改数据库中记录状态
//                        Integer saveOkFileStats = FileUtil.saveOkFile("/Users/4paradigm/test/cal_index/" + fileName);

//                        Integer getFileStatus = ShellUtil.shellExecute("hdfs dfs -get " + hdfsPath + "/" + statusFutures.getFileName() + "/" + fileName + " " + localFile);
                        Integer saveOkFileStats = FileUtil.saveOkFile(filePath + "/" + fileName); // 会执行两次这个代码
                        // 从hdfs 拉取保存好的文件到本地
//                        String localFile = filePath + "/" + fileName;
                        if (saveOkFileStats == 0) {
//                        if (getFileStatus == 0 && saveOkFileStats == 0) {
                            logger.info("download calIndex success, data save filePath:" + filePath + "/" + statusFutures.getFileName());
                            this.calIndexRecordMapper.updateById(new CalIndexRecord(statusFutures.getId(), 1));
                            map.remove(s);
                        } else {
                            logger.error("calIndex task execute success, save okFile exception...");
                            this.calIndexRecordMapper.updateById(new CalIndexRecord(statusFutures.getId(), 2));
                            map.remove(s);
                        }
                    }

                }
            } else {
                Thread.sleep(100);
            }

        }
    }
}
