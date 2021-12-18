package com.catl.cal_index.controller;

import com.catl.cal_index.entity.calIndex.*;
import com.catl.cal_index.mapper.calIndex.*;
import com.catl.cal_index.request.CalIndexReq;
import com.catl.cal_index.request.ParamNameReq;
import com.catl.cal_index.service.calIndexService;
import com.catl.cal_index.vo.CalIndexResultVo;
import com.catl.cal_index.vo.CalIndexTypeVo;
import com.catl.cal_index.vo.CarBatteryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@Api(value = "/calIndex", tags = "指标计算")
@RequestMapping("/calIndex")
public class CalIndexController {
    @Resource
    private calIndexService calIndexService;
    @Resource
    private CalIndexTypeMapper calIndexTypeMapper;
    @Resource
    private CalIndexParamMapper calIndexParamMapper;
    @Resource
    private VinMapperMapper vinMapperMapper;
    @Resource
    private CalIndexRecordMapper calIndexRecordMapper;

    @PostMapping("/getFileByCalIndex")
    @ApiOperation("根据传入的 calIndex和calVars 计算对应车辆的信息，返回文件下载路径")
    public String getFileByCalIndex(@RequestBody CalIndexReq calIndexReq) {
        return calIndexService.getFileByCalIndex(calIndexReq.getCalIndex(), calIndexReq.getParams(),
                calIndexReq.getDownloadStats());
    }

    @PostMapping("/getTaskStatusByCalIndex")
    @ApiOperation("根据传入的 CalIndex和calVars，返回文件查询状态")
    public CalIndexRecord getTaskStatusByCalIndex(@RequestBody CalIndexReq calIndexReq) {
        return calIndexService.getTaskStatusByCalIndex(calIndexReq.getCalIndex(), calIndexReq.getParams());
    }

    @ApiOperation("查看计算结果数量是否小于1000，小于1000将对应数据返回，大于50返回空")
    @PostMapping("/getCalIndexResultNumber")
    public CalIndexResultVo getCalIndexResultNumber(@RequestBody CalIndexReq calIndexReq) {
        return calIndexService.getCalIndexResultNumber(calIndexReq);
    }

    @GetMapping("/downCalIndexFile")
    @ApiOperation("下载对应指标计算的计算结果文件")
    public String downCalIndexFile(String fileName, HttpServletResponse resp) {
        return calIndexService.downCalIndexFile(fileName, resp);
    }

    @GetMapping("/getAllCalIndexType")
    @ApiOperation("根据指标类别获取指标计算类型")
    public List<CalIndexTypeVo> getAllCalIndexType() {
        return calIndexService.getAllCalIndexType();
    }

    @PostMapping("/insertCalIndexType")
    @ApiOperation("插入相应指标")
    public boolean insertCalIndexType(@RequestBody CalIndexType calIndexType) {
        return calIndexTypeMapper.insert(calIndexType) > 0;
    }

    @PostMapping("/insertCalIndexParam")
    @ApiOperation("插入相应指标参数")
    public boolean insertCalIndexParam(@RequestBody CalIndexParam calIndexParam) {
        return calIndexParamMapper.insert(calIndexParam) > 0;
    }

    @GetMapping("/getBatteryList")
    @ApiOperation("获取电池型号列表")
    public List<CarBatteryVo> getBatteryList() {
        return calIndexService.getBatteryList();
    }

    @GetMapping("/getAutomobileEnterpriseByBattery")
    @ApiOperation("根据电池型号获取车企列表")
    public List<CarBatteryVo> getAutomobileEnterpriseByBattery(String battery) {
        return calIndexService.getAutomobileEnterpriseByBattery(battery);
    }

    @GetMapping("/getVinMapper")
    @ApiOperation("获取 vin明文，或者 vin密文")
    public VinMapper getVinMapper(String vin) {
        return (vinMapperMapper.getVinOrMvinByVin(vin));
    }

    @PostMapping("/getLatestSuccessRecord")
    @ApiOperation("根据 calIndex 和 参数查询最近一次成功")
    public CalIndexRecord getLatestSuccessRecord(@RequestBody CalIndexReq calIndexReq) {
        String calVarsString = calIndexReq.getParams().stream().map(ParamNameReq::getParamValue).collect(Collectors.joining("-"));
        return calIndexRecordMapper.getLatestSuccessRecord(calIndexReq.getCalIndex(), calVarsString);
    }

    @PostMapping("/getLatestRunningRecord")
    @ApiOperation("根据 calIndex 和 参数查询最近一次正在运行")
    public CalIndexRecord getLatestRunningRecord(@RequestBody CalIndexReq calIndexReq) {
        String calVarsString = calIndexReq.getParams().stream().map(ParamNameReq::getParamValue).collect(Collectors.joining("-"));
        return calIndexRecordMapper.getLatestRunningRecord(calIndexReq.getCalIndex(), calVarsString);
    }

    @GetMapping("/getRecordListByCalIndex")
    @ApiOperation("查询该指标的--历史记录")
    public String getRecordListByCalIndex(String calIndex, HttpServletResponse resp) {
        return calIndexService.getRecordListByCalIndex(calIndex, resp);
    }

}
