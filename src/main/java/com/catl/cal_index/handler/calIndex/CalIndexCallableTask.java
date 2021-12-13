package com.catl.cal_index.handler.calIndex;

import com.alibaba.fastjson.JSONObject;
import com.catl.cal_index.entity.calIndex.CalIndexParam;
import com.catl.cal_index.entity.calIndex.CalIndexRecord;
import com.catl.cal_index.entity.calIndex.CalIndexType;
import com.catl.cal_index.mapper.calIndex.CalIndexParamMapper;
import com.catl.cal_index.mapper.calIndex.CalIndexRecordMapper;
import com.catl.cal_index.mapper.calIndex.CalIndexTypeMapper;
import com.catl.cal_index.request.ParamNameReq;
import com.catl.cal_index.utils.ShellUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalIndexCallableTask implements Callable<List<String>> {

    public static final Logger logger = LoggerFactory.getLogger(CalIndexCallableTask.class);

    private String calIndex;
    private String calVars;
    private List<ParamNameReq> params;
    private CalIndexRecordMapper calIndexRecordMapper;
    private CalIndexParamMapper calIndexParamMapper;
    private CalIndexTypeMapper calIndexTypeMapper;
    private String dirPath;
    private String hdfsPath;
    private String filePath;
    private CalIndexRecord calIndexRecord;

    @Override
    @Transactional
    public List<String> call() throws Exception {
        logger.info("all parameters value，calIndex：{}, calVars：{}, params:{}, calIndexRecordMapper:{} ," +
                "calIndexParamMapper:{}, calIndexTypeMapper:{}, dirPath:{}, hdfsPath:{}, filePath:{}, calIndexRecord:{}",
                calIndex, calVars, JSONObject.toJSON(params), JSONObject.toJSON(calIndexRecordMapper),
                JSONObject.toJSON(calIndexParamMapper), JSONObject.toJSON(calIndexTypeMapper),
                dirPath, hdfsPath, filePath, JSONObject.toJSON(calIndexRecord));

        // dirPath = dfsPath + "/" + calIndex_varsString + "_" + dateTime
        String[] split = dirPath.split("/");
        String fileName = split[split.length - 1];
        calIndexRecord.setCalIndex(calIndex);
        calIndexRecord.setCalVars(calVars);
        calIndexRecord.setResult(fileName);
        calIndexRecord.setStatus(0);
        System.out.println("CalIndexCallableTask --------- filename:" + fileName);
        System.out.println("CalIndexCallableTask --------- dirPath:" + dirPath);

        // 拼接参数，运行 runcode.sh， 把拼接的参数传递进去
        List<CalIndexParam> allParentParam = calIndexParamMapper.getAllParentParam();
        CalIndexType typeByCalIndex = calIndexTypeMapper.getTypeByCalIndex(calIndex);

        // 拼接命令，把py文件，runcode.sh文件，以及参数拼接起来，用于执行shell脚本
        StringBuilder command = new StringBuilder("/bin/bash " + typeByCalIndex.getRunStorePath());
        command.append(" ").append("-p").append(" ").append(typeByCalIndex.getPyStorePath());
        for (CalIndexParam calIndexParam : allParentParam) {
            for (ParamNameReq param : params) {
                if(param.getParam().equals(calIndexParam.getParam())){
                    command.append(" ").append(calIndexParam.getParamFormal())
                            .append(" ").append(param.getParamValue());
                }
            }
        }
        command.append(" ").append("-n").append(" ").append(fileName);
        command.append(" ").append("-d").append(" ").append(dirPath);

        calIndexRecordMapper.updateById(calIndexRecord);

        System.out.println("shellUtil--------》》》》》》》。 command："+ command);
        logger.info("ShellUtil------ command : " + command);
        Integer status = ShellUtil.shellExecute(command.toString());

        String localFile = filePath.concat("/").concat(fileName);
//        String getFileCommand = "hdfs dfs -get ".concat(hdfsPath).concat("/").concat(fileName).concat("/")
//                .concat(fileName).concat(" ").concat(localFile);
//        Integer getFileStatus = ShellUtil.shellExecute(getFileCommand);
//        if (status == 0 && getFileStatus ==0) {
        if (status == 0) {
            calIndexRecordMapper.updateById(new CalIndexRecord(calIndexRecord.getId(), 0));
        } else {
//            status = ShellUtil.shellExecute(commend);
            status = ShellUtil.shellExecute(command.toString());
//            getFileStatus = ShellUtil.shellExecute(getFileCommand);
//            if (status == 0 && getFileStatus == 0) {
            if (status == 0 ) {

//                calIndexRecordMapper.updateById(new CalIndexRecord(record.getId(), 1));
                calIndexRecordMapper.updateById(new CalIndexRecord(calIndexRecord.getId(), 0));
            } else {
                calIndexRecordMapper.updateById(new CalIndexRecord(calIndexRecord.getId(), 2));
            }
        }
        ArrayList<String> list = new ArrayList<>();
        list.add(fileName);
        list.add(status.toString());
        logger.info("future's fileName:{} , status:{}", fileName, status);
        return list;
    }
}
