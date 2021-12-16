package com.catl.cal_index.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.catl.cal_index.entity.calIndex.*;
import com.catl.cal_index.manage.calIndex.CalIndexManage;
import com.catl.cal_index.mapper.calIndex.*;
import com.catl.cal_index.request.CalIndexReq;
import com.catl.cal_index.request.ParamNameReq;
import com.catl.cal_index.service.calIndexService;
import com.catl.cal_index.utils.BeanUtils;
import com.catl.cal_index.utils.FileUtil;
import com.catl.cal_index.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CalIndexServiceImpl implements calIndexService {

    public Logger logger = LoggerFactory.getLogger(CalIndexServiceImpl.class);

    @Resource
    private CalIndexRecordMapper calIndexRecordMapper;
    @Resource
    private CalIndexTypeMapper calIndexTypeMapper;
    @Resource
    private CalIndexParamMapper calIndexParamMapper;
    @Resource
    private CarBatteryMapMapper carBatteryMapMapper;
    @Resource
    private VinMapperMapper vinMapperMapper;

    @Value("${filePath}")
    private String FILE_PATH;
    @Value("${hdfsPath}")
    private String HDFS_PATH;
    @Value("${max_limit_number}")
    private String MAX_LIMIT_NUMBER;

    @Override
    public String getFileByCalIndex(String calIndex, List<ParamNameReq> params, Integer downloadStats) {
        logger.info("各个参数的值 calIndex:{} params:{} downloadStats:{}", calIndex, JSONObject.toJSON(params), downloadStats);
        // calVarsString 是用"_"拼接好的字符串
        String calVarsString = params.stream().map(ParamNameReq::getParamValue).collect(Collectors.joining("-"));
        String calIndexVarsDir = calIndex.concat("_").concat(calVarsString);
        logger.info("参数拼接之后的值是  calVarsString :{}  calIndexVarsDir: {}", calVarsString, calIndexVarsDir);

        //不存在则创建
        // 日期格式化到分钟
        String dateTime = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String dir = calIndexVarsDir + "_" + dateTime;
        if (!FileUtil.createDir(FILE_PATH, dir, downloadStats)) {
            logger.info(" create filePath exception !!!!");
            return "create filePath exception";
        }

        // 提交任务之前，对上汽148ah的vin明文，密文做一个转换
        params.forEach(
                obj->{
                    if("vin".equals(obj.getParam()) && "LIST".equals(obj.getParamType())){
                        VinMapper vinOrMvinByVin = vinMapperMapper.getVinOrMvinByVin(obj.getParamValue());
                        if(null != vinOrMvinByVin){
                            obj.setParamValue(vinOrMvinByVin.getVin().concat(",").concat(vinOrMvinByVin.getMvin()));
                            logger.info("明密文转换之后的值是： {}", JSONObject.toJSON(params));
                        }
                    }
                }
        );

        //提交任务到manage
        CalIndexManage manage = CalIndexManage.getManage();
        if (!manage.submit(calIndex, calVarsString, dateTime, params)) {
            logger.info(" An task already running, please hold on a second!!");
            return calIndex + "  An existing task is running...";
        }
        logger.info("An task is running !!!");
        return "The current task is running";
    }

    @Override
    public CalIndexRecord getTaskStatusByCalIndex(String calIndex, List<ParamNameReq> params) {
        logger.info("getTaskStatusByCalIndex accept parameter ：{},{} ", calIndex, JSONObject.toJSON(params));
        // calVarsString 是用"-"拼接好的字符串
        String calVarsString = params.stream().map(ParamNameReq::getParamValue).collect(Collectors.joining("-"));
        CalIndexRecord calIndexRecord = calIndexRecordMapper.getTaskStatusByCalIndex(calIndex, calVarsString);
        return null == calIndexRecord ? new CalIndexRecord() : calIndexRecord;
    }

    @Override
    public String downCalIndexFile(String fileName, HttpServletResponse response) {
        logger.info("downCalIndexFile accept parameter: fileName: {} ", fileName);
        // compress file
        File file1 = new File(FILE_PATH + "/" + fileName + "/" + fileName + ".zip");
        if (!file1.exists()) {
            if (!FileUtil.fileToZip(FILE_PATH + "/" + fileName + "/" + fileName,
                    FILE_PATH + "/" + fileName, fileName)) {
                logger.info(" file compression fail，please retry query！ compression path:{}", file1.getAbsolutePath());
                return "file compression fail，please retry query！";
            }
        }

        // if compression not fail, then compression and download file!
        File file = new File(FILE_PATH + "/" + fileName + "/" + fileName + ".zip");
        FileInputStream fis = null;
        ServletOutputStream out = null;
        try {
            logger.info("start download file...");

            fis = new FileInputStream(file);
            int len;
            byte[] buffer = new byte[1024 * 10];
            response.setContentType("multipart/form-data");
            //为文件重新设置名字，采用数据库内存储的文件名称
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName + ".zip");
            out = response.getOutputStream();
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            logger.info("download file error，because of：{}", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("download file success！");
        return "download success!";
    }

    @Override
    public List<CalIndexTypeVo> getAllCalIndexType() {
        // 获取所有指标类型
        List<CalIndexType> allCalIndexType = calIndexTypeMapper.getAllCalIndexType();
        // 获取所有父类
        List<ParamNameVo> allParentParam = BeanUtils.copyList(
                calIndexParamMapper.getAllParentParam(), ParamNameVo.class);
        // 获取所有子类
        List<CalIndexParam> allSonParam = calIndexParamMapper.getAllSonParam();

        // 将父子参数进行封装
        Map<Integer, List<CalIndexParam>> groupByPid = allSonParam.stream()
                .collect(Collectors.groupingBy(CalIndexParam::getPid));
        allParentParam.forEach(
                obj -> {
                    List<CalIndexParam> calIndexParams = groupByPid.get(obj.getId());
                    if (!CollectionUtils.isEmpty(calIndexParams)) {
                        //  子参数  如 时间分割段[日、周、月、季度]等
                        List<ChildrenParamNameVo> options = calIndexParams.stream().map(param -> new ChildrenParamNameVo(
                                        param.getParamName(), param.getParam(), param.getParamType()))
                                .collect(Collectors.toList());
                        obj.setOptions(options);
                    }
                }
        );

        // 将封装过后的父子参数封装到 calIndexTypeSubVo 中
        Map<String, List<ParamNameVo>> groupByCalIndex = allParentParam.stream()
                .collect(Collectors.groupingBy(ParamNameVo::getCalIndex));

        // 根据指标大类型 cal_type 分组，
        Map<String, List<CalIndexType>> groupByCalType = allCalIndexType.stream()
                .collect(Collectors.groupingBy(CalIndexType::getCalType));
        ArrayList<CalIndexTypeVo> resultList = new ArrayList<>();
        groupByCalType.forEach(
                (key, value) -> {
                    // 封装相应参数
                    if (!CollectionUtils.isEmpty(value)) {
                        CalIndexTypeVo calIndexTypeVo = BeanUtils.copyObject(value.get(0), CalIndexTypeVo.class);
                        List<CalIndexTypeSubVo> calIndexTypeSubVos = BeanUtils.copyList(value, CalIndexTypeSubVo.class);

                        // 对每个具体指标封装对应参数
                        calIndexTypeSubVos.forEach(
                                calIndexTypeSubVo -> {
                                    List<ParamNameVo> nameVos = groupByCalIndex.get(calIndexTypeSubVo.getCalIndex());
                                    calIndexTypeSubVo.setParamNameVos(nameVos);
                                }
                        );

                        calIndexTypeVo.setCalIndexTypeSubVos(calIndexTypeSubVos);
                        resultList.add(calIndexTypeVo);
                    }
                }
        );

        logger.info("Index calculation all parameter：{}", JSONObject.toJSON(resultList));
        return resultList;
    }

//    @Override
//    public CalIndexResultVo getCalIndexResultNumber(CalIndexReq calIndexReq) {
//        CalIndexResultVo resultVo = new CalIndexResultVo();
//        String calVarsString = calIndexReq.getParams().stream().map(ParamNameReq::getParamValue).collect(Collectors.joining("-"));
//
//        String fileName = "";
//        // 判断是否要强制查询, 如果为
//        if(calIndexReq.getDownloadStats() == 0){
//            // 找到下载好的文件名
//            CalIndexRecord taskStatusByCalIndex = calIndexRecordMapper.getTaskStatusByCalIndex(calIndexReq.getCalIndex(), calVarsString);
//            fileName = taskStatusByCalIndex.getResult();
//        }else{
//            CalIndexRecord latestSuccessRecord = calIndexRecordMapper.getLatestSuccessRecord(calIndexReq.getCalIndex(), calVarsString);
//            fileName = latestSuccessRecord.getResult();
//        }
//        logger.info("getCalIndexResultNumber, accept parameters: {}", JSONObject.toJSON(calIndexReq));
//        logger.info("getCalIndexResultNumber, fileName: {}", fileName);
//
//        CalIndexType typeByCalIndex = calIndexTypeMapper.getTypeByCalIndex(calIndexReq.getCalIndex());
//
//        // 计算结果要展示的类型 Scatter 散点图，Pie 饼图；Line 折线图，Bar 柱状图
//        resultVo.setResultType(typeByCalIndex.getResultType());
//        // 计算结果内容 单车 SINGLE  多车 MULTI
//        String carNumberType = typeByCalIndex.getCarNumberType();
//        resultVo.setCarNumberType(carNumberType);
//        // 多车也做数据展示，如果是数据量不超过5000
//        logger.info(" getCalIndexResultNumber , resultVo:{} ", JSONObject.toJSON(resultVo));
//
//        // java 读取 csv 文件
//        File file = new File(FILE_PATH + "/" + fileName + "/" + fileName);
//        if (!file.exists()) {
//            resultVo.setResultNum(-1); // 如果文件不存在，证明查询程序没有查处数据，需要重新查询
//            logger.info("csv file is not exist!!!!!!");
//            logger.info(" getCalIndexResultNumber , resultVo:{} ", JSONObject.toJSON(resultVo));
//            return resultVo;
//        }
//
//        File[] files = file.listFiles();
//        if (null != files && files.length != 0) {
//            for (File objFile : files) {
//                if (objFile.getName().endsWith(".csv")) {
//                    try {
//                        // 统计csv 文件到底有多少行
//                        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(objFile));
//                        lineNumberReader.skip(Long.MAX_VALUE); // 跳到最后
//                        int lines = lineNumberReader.getLineNumber(); //实际上是读取换行符数量
//                        resultVo.setResultNum(lines);
//                        lineNumberReader.close();
//                        logger.info(" count csv file line number：{} ", lines);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    // vin对应图表x轴，y轴数据
//                    Map<String,ContentChartVo> vinToContentChartMap = new HashMap<>();
//                    //小于等于5000行就进一步处理成前端要的数据模样 ,!=-1代表存在，<=5000 可以展示数据
////                    if (resultVo.getResultNum() <= Long.MAX_VALUE && resultVo.getResultNum() != -1) {
//                    if (resultVo.getResultNum() != -1) {
//                        try {
//                            // 如果是多车，把所有的vin读取出来，然后分组封装
//                            InputStreamReader isr = new InputStreamReader(new FileInputStream(objFile), StandardCharsets.UTF_8);
//                            BufferedReader br = new BufferedReader(isr);
//                            String line = "";
//
//                            int RowCount = 0; // csv 行数
//
//                            int vinLocationColumn = 0; // vin 在数组的第几列,,经过 spark 处理后的csv默认第一列是vin
//                            int carTypeColumn = 0; // 汽车类型所在列
//                            int percentLocationColumn1 = 0; // 百分比在数组的第几列
//                            // 百分比的数据列名，一般默认为 percent
//                            String pieColumn = typeByCalIndex.getPieColumn();
//
//                            // 如果是折线图、散点图、柱状图等
//                            int xLocationColumn = 0;  // x 轴数据所在列
//                            int yLocationColumn = 0;  // y 轴数据所在列
////                                int carTypeColumn2 = 0; // 汽车类型所在列
//                            String xColumn = typeByCalIndex.getXColumn();
//                            String yColumn = typeByCalIndex.getYColumn();
//
//                            while (line != null) {
//                                ContentChartVo chartVo = new ContentChartVo();
//
//                                RowCount++;
//                                line = br.readLine(); // line 逗号分割好的，如第一行表头 vin,datatime,temp_diff,car_type
//                                if (null == line || line.length() == 0) {
//                                    continue; // 跳过空行
//                                }
//                                // 对读取出来的一行进行逗号分割
//                                String[] splitArray = line.split(",");
//
//                                if (typeByCalIndex.getResultType().equals("Pie")) { // 如果是饼图
//                                    // 如果是首行
////                                            String[] splitArray1 = line.split(",");
//                                    for (int i = 0; i < splitArray.length && RowCount ==1; i++) {
//                                        if (splitArray[i].equals(pieColumn)) {
//                                            percentLocationColumn1 = i;
//                                        }
//                                        // csv 中 汽车类型定死的列名
//                                        if ("car_type".equals(splitArray[i])) {
//                                            carTypeColumn = i;
//                                        }
//                                        if("vin".equals(splitArray[i])){
//                                            vinLocationColumn = i;
//                                        }
//
//                                        logger.info("if Pie, percentLocationColumn1:{}, carTypeColumn:{}, vinLocationColumn:{}",
//                                                percentLocationColumn1, carTypeColumn, vinLocationColumn);
//                                    }
//                                    if (RowCount != 1) { // 不写表头
//                                        // 更新 map 中的 数据
//                                        ContentChartVo contentChartVo = vinToContentChartMap.get(splitArray[vinLocationColumn]);
//                                        if(null == contentChartVo){ // 第一次更新数据
//                                            chartVo.setVin(splitArray[vinLocationColumn]);
//                                            chartVo.setCarType(splitArray[carTypeColumn]);
//                                            // 如果取到的数据为空，就置为0，如果不为空，就直接设置进去
//                                            chartVo.setPercent(Double.parseDouble(
//                                                    (null == splitArray[percentLocationColumn1] || "\"\"".equals(splitArray[percentLocationColumn1]))? "0":splitArray[percentLocationColumn1]));
//                                            // 推送到 vin对应 contentChart的map 上
//                                            vinToContentChartMap.put(splitArray[vinLocationColumn],chartVo);
//                                        }
//                                    }
//                                } else {
//                                    // 如果是首行
//                                    for (int i = 0; i < splitArray.length && RowCount ==1; i++) {
//                                        if (splitArray[i].equals(xColumn)) {
//                                            xLocationColumn = i;
//                                        }
//                                        if (splitArray[i].equals(yColumn)) {
//                                            yLocationColumn = i;
//                                        }
//                                        // csv 中 汽车类型定死的列名
//                                        if ("car_type".equals(splitArray[i])) {
//                                            carTypeColumn = i;
//                                        }
//                                        if("vin".equals(splitArray[i])){
//                                            vinLocationColumn = i;
//                                        }
//                                        logger.info("if not Pie, xLocationColumn:{}, yLocationColumn:{}, carTypeColumn:{}， vinLocationColumn：{}",
//                                                xLocationColumn, yLocationColumn, carTypeColumn, vinLocationColumn);
//                                    }
//                                    if (RowCount != 1) { // 不写表头
//                                        // 更新 map 中的 数据
//                                        ContentChartVo contentChartVo = vinToContentChartMap.get(splitArray[vinLocationColumn]);
//                                        if(null == contentChartVo) { // 第一次添加数据
//                                            chartVo.setVin(splitArray[vinLocationColumn]);
//                                            // 添加汽车类型
//                                            chartVo.setCarType(splitArray[carTypeColumn]);
//
//                                            // 添加x轴，y轴数据，组装成[[x1,y1],[x2,y2],[x3,y3]]这种形式
//                                            ArrayList<Object> dataList = new ArrayList<>();
//                                            dataList.add(splitArray[xLocationColumn]);
//                                            // 如果取到的数据为空，就置为0，如果不为空，就直接设置进去
//                                            dataList.add(((null == splitArray[yLocationColumn] || "\"\"".equals(splitArray[yLocationColumn]))? "0":splitArray[yLocationColumn]));
//                                            // 添加数据
//                                            List<List<Object>> dataPoint = new ArrayList<>();
//                                            dataPoint.add(dataList);
//                                            chartVo.setDataPoint(dataPoint);
//
//                                            // 推送到 vin对应 contentChart的map 上
//                                            vinToContentChartMap.put(splitArray[vinLocationColumn],chartVo);
//                                        }else if(splitArray[vinLocationColumn].equals(contentChartVo.getVin())){
//                                            // 添加x轴，y轴数据，组装成[[x1,y1],[x2,y2],[x3,y3]]这种形式
//                                            ArrayList<Object> dataList = new ArrayList<>();
//                                            dataList.add(splitArray[xLocationColumn]);
//                                            // 如果取到的数据为空，就置为0，如果不为空，就直接设置进去
//                                            dataList.add(((null == splitArray[yLocationColumn] || "\"\"".equals(splitArray[yLocationColumn]))? "0":splitArray[yLocationColumn]));
//                                            // 添加数据
//                                            List<List<Object>> dataPoint = contentChartVo.getDataPoint();
//                                            dataPoint.add(dataList);
//                                            contentChartVo.setDataPoint(dataPoint);
//
//                                            // 推送到 vin对应 contentChart的map 上
//                                            vinToContentChartMap.put(splitArray[vinLocationColumn],contentChartVo);
//                                        }
//                                    }
//                                }
//                            }
//
//                            // 将多车数据封装进vo中
//                            List<ContentChartVo> contentChartVoList = new ArrayList<>();
//                            vinToContentChartMap.forEach((key,value)-> contentChartVoList.add(value));
//                            resultVo.setResultContentsList(contentChartVoList);
////                            logger.info("resultContentsList:{}", JSONObject.toJSON(contentChartVoList));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            logger.info("get result number error: {}", e.getMessage());
//                        }
//                    }
//                }
//            }
//        }
//
//        // 所有都封装好了，最后再返回
//        return resultVo;
//    }

    @Override
    public CalIndexResultVo getCalIndexResultNumber(CalIndexReq calIndexReq) {
        CalIndexResultVo resultVo = new CalIndexResultVo();
        String calVarsString = calIndexReq.getParams().stream().map(ParamNameReq::getParamValue).collect(Collectors.joining("-"));

        String fileName = "";
        // 判断是否要强制查询, 如果为
        if(calIndexReq.getDownloadStats() == 0){
            // 找到下载好的文件名
            CalIndexRecord taskStatusByCalIndex = calIndexRecordMapper.getTaskStatusByCalIndex(calIndexReq.getCalIndex(), calVarsString);
            fileName = taskStatusByCalIndex.getResult();
        }else{
            CalIndexRecord latestSuccessRecord = calIndexRecordMapper.getLatestSuccessRecord(calIndexReq.getCalIndex(), calVarsString);
            fileName = latestSuccessRecord.getResult();
        }
        logger.info("getCalIndexResultNumber, accept parameters: {}", JSONObject.toJSON(calIndexReq));
        logger.info("getCalIndexResultNumber, fileName: {}", fileName);

        CalIndexType typeByCalIndex = calIndexTypeMapper.getTypeByCalIndex(calIndexReq.getCalIndex());

        // 计算结果要展示的类型 Scatter 散点图，Pie 饼图；Line 折线图，Bar 柱状图
        resultVo.setResultType(typeByCalIndex.getResultType());
        // 计算结果内容 单车 SINGLE  多车 MULTI
        String carNumberType = typeByCalIndex.getCarNumberType();
        resultVo.setCarNumberType(carNumberType);
        // 多车也做数据展示，如果是数据量不超过5000
        logger.info(" getCalIndexResultNumber , resultVo:{} ", JSONObject.toJSON(resultVo));

        // java 读取 csv 文件
        File file = new File(FILE_PATH + "/" + fileName + "/" + fileName);
        if (!file.exists()) {
            resultVo.setResultNum(-1); // 如果文件不存在，证明查询程序没有查处数据，需要重新查询
            logger.info("csv file is not exist!!!!!!");
            logger.info(" getCalIndexResultNumber , resultVo:{} ", JSONObject.toJSON(resultVo));
            return resultVo;
        }

        File[] files = file.listFiles();
        if (null != files && files.length != 0) {
            for (File objFile : files) {
                if (objFile.getName().endsWith(".csv")) {
                    try {
                        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(objFile));
                        lineNumberReader.skip(Long.MAX_VALUE); // 跳到最后
                        int lines = lineNumberReader.getLineNumber(); //实际上是读取换行符数量
                        resultVo.setResultNum(lines);
                        lineNumberReader.close();
                        logger.info(" count csv file line number：{} ", lines);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Map<String, List<String>> resultMap = new HashMap<>();
                    //小于等于5000行就进一步处理成前端要的数据模样 ,!=-1代表存在，<=5000 可以展示数据
                    if (resultVo.getResultNum() <= Long.parseLong(MAX_LIMIT_NUMBER) && resultVo.getResultNum() != -1) {
                        try {
                            // 如果是多车，把所有的vin读取出来，然后分组封装
                            InputStreamReader isr = new InputStreamReader(new FileInputStream(objFile), StandardCharsets.UTF_8);
                            BufferedReader br = new BufferedReader(isr);
                            String line = "";

                            int RowCount = 0; // csv 行数
                            int carTypeColumn = 0; // 汽车类型所在列

                            // 如果是折线图、散点图、柱状图等
                            int xLocationColumn = 0;  // x 轴数据所在列
                            int yLocationColumn = 0;  // y 轴数据所在列
                            String xColumn = typeByCalIndex.getXColumn();
                            String yColumn = typeByCalIndex.getYColumn();

                            ContentChartVo chartVo = new ContentChartVo();
                            while (line != null) {

                                RowCount++; // 代表 csv 行数
                                line = br.readLine(); // line 逗号分割好的，如第一行表头 vin,datatime,temp_diff,car_type
                                if (null == line || line.length() == 0) {
                                    continue; // 跳过空行
                                }
                                // 对读取出来的一行进行逗号分割
                                String[] splitArray = line.split(",");
                                // 如果是首行
                                if(RowCount == 1){
                                    for (int i = 0; i < splitArray.length; i++) {
                                        if (splitArray[i].equals(xColumn)) {
                                            xLocationColumn = i;
                                        }
                                        if (splitArray[i].equals(yColumn)) {
                                            yLocationColumn = i;
                                        }
                                        // csv 中 汽车类型定死的列名
                                        if ("car_type".equals(splitArray[i])) {
                                            carTypeColumn = i;
                                        }
                                        logger.info("if not Pie, xLocationColumn:{}, yLocationColumn:{}, carTypeColumn:{}",
                                                xLocationColumn, yLocationColumn, carTypeColumn);
                                    }

                                    // 添加汽车类型
                                    chartVo.setCarType(splitArray[carTypeColumn]);
                                    List<String> dataList1 = new ArrayList<>();
                                    dataList1.add(splitArray[xLocationColumn]);
                                    // 如果取到的数据为空，就置为0，如果不为空，就直接设置进去
                                    dataList1.add(((null == splitArray[yLocationColumn] || "\"\"".equals(splitArray[yLocationColumn]))? "0":splitArray[yLocationColumn]));
                                    resultMap.put(splitArray[xLocationColumn], dataList1);
                                }
                                if (RowCount != 1) { // 不写表头
                                    // 添加x轴，y轴数据，组装成[[x1,y1],[x2,y2],[x3,y3]]这种形式
                                    List<String> dataList2 = new ArrayList<>();
                                    dataList2.add(splitArray[xLocationColumn]);
                                    // 如果取到的数据为空，就置为0，如果不为空，就直接设置进去
                                    dataList2.add(((null == splitArray[yLocationColumn] || "\"\"".equals(splitArray[yLocationColumn]))? "0":splitArray[yLocationColumn]));
                                    resultMap.put(splitArray[xLocationColumn], dataList2);
                                }
                            }

                            // 将 map 中的数据封装到 resultVo 中
                            List<List<String>> resultDataPoint = chartVo.getDataPoint();
                            resultMap.forEach((key,value)->{
                                resultDataPoint.add(value);
                            });
                            chartVo.setDataPoint(resultDataPoint);
                            chartVo.setDataNum(resultDataPoint.size());

                            // 封装成 list， 方便前端取数据
                            List<ContentChartVo> temp = new ArrayList<>();
                            temp.add(chartVo);
                            resultVo.setResultContentsList(temp);

                        } catch (IOException e) {
                            e.printStackTrace();
                            logger.info("get result number error: {}", e.getMessage());
                        }
                    }
                }
            }
        }

        // 所有都封装好了，最后再返回
        return resultVo;
    }

    @Override
    public List<CarBatteryVo> getBatteryList() {
        List<CarBatteryMap> batteryList = carBatteryMapMapper.getBatteryList();
        if(CollectionUtils.isEmpty(batteryList)){return new ArrayList<>();}
        // 拼接成对应的形式并返回
        return batteryList.stream().map(obj->new CarBatteryVo(obj.getId(),obj.getCap(),
                obj.getCap(),"SerchSelect")).collect(Collectors.toList());

    }

    @Override
    public List<CarBatteryVo> getAutomobileEnterpriseByBattery(String battery) {
        List<CarBatteryMap> batteryList = carBatteryMapMapper.getAutomobileEnterpriseByBattery(battery);
        if(CollectionUtils.isEmpty(batteryList)){return new ArrayList<>();}
        // 拼接成对应的形式并返回
        return batteryList.stream().map(obj->new CarBatteryVo(obj.getId(),obj.getCar(),
                obj.getCar(),"SerchSelect")).collect(Collectors.toList());
    }

}
