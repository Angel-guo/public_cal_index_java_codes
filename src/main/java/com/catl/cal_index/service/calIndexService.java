package com.catl.cal_index.service;

import com.catl.cal_index.entity.calIndex.CalIndexRecord;
import com.catl.cal_index.request.CalIndexReq;
import com.catl.cal_index.request.ParamNameReq;
import com.catl.cal_index.vo.CalIndexResultVo;
import com.catl.cal_index.vo.CalIndexTypeVo;
import com.catl.cal_index.vo.CarBatteryVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface calIndexService {

    /**
     * 执行指标查询
     * @param calIndex
     * @param params
     * @param downloadStats
     * @return
     */
    String getFileByCalIndex(String calIndex, List<ParamNameReq> params, Integer downloadStats);

    /**
     *  根据 calIndex 和 params 查询指标计算运行状态
     * @param calIndex
     * @param params
     * @return
     */
    CalIndexRecord getTaskStatusByCalIndex(String calIndex, List<ParamNameReq> params);

    /**
     *  下载文件
     * @param fileName
     * @param resp
     * @return
     */
    String downCalIndexFile(String fileName, HttpServletResponse resp);

    /**
     * 获取所有的指标计算类型
     * @return
     */
    List<CalIndexTypeVo> getAllCalIndexType();

    /**
     *  根据指标查询参数，获取指标计算结果和数据展示内容
     * @param calIndexReq
     * @return
     */
    CalIndexResultVo getCalIndexResultNumber(CalIndexReq calIndexReq);

    /**
     * 获取电池型号列表
     * @return
     */
    List<CarBatteryVo> getBatteryList();

    /**
     * 根据电池型号获取汽车企业列表
     * @param battery
     * @return
     */
    List<CarBatteryVo> getAutomobileEnterpriseByBattery(String battery);

    /**
     * 查询该指标所有的查询记录
     * @param calIndex
     * @return
     */
    String getRecordListByCalIndex(String calIndex, HttpServletResponse resp);
}
