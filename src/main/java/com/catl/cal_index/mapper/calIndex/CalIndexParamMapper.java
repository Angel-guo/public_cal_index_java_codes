package com.catl.cal_index.mapper.calIndex;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catl.cal_index.entity.calIndex.CalIndexParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CalIndexParamMapper extends BaseMapper<CalIndexParam> {

    /**
     * 获取所有的父类参数
     * @return
     */
    List<CalIndexParam> getAllParentParam();

    /**
     * 获取所有的子类参数
     * @return
     */
    List<CalIndexParam> getAllSonParam();
}
