package com.catl.cal_index.config;

import com.catl.cal_index.manage.calIndex.CalIndexManage;
import com.catl.cal_index.mapper.calIndex.CalIndexParamMapper;
import com.catl.cal_index.mapper.calIndex.CalIndexRecordMapper;
import com.catl.cal_index.mapper.calIndex.CalIndexTypeMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DemoApplicationRunner implements ApplicationRunner {
    @Resource
    private CalIndexRecordMapper calIndexRecordMapper;
    @Resource
    private CalIndexParamMapper calIndexParamMapper;
    @Resource
    private CalIndexTypeMapper calIndexTypeMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CalIndexManage.getManage().initMapper(calIndexRecordMapper, calIndexParamMapper, calIndexTypeMapper);
        // 初始化指标计算
        CalIndexManage.getManage().initCalIndexTask();
    }
}
