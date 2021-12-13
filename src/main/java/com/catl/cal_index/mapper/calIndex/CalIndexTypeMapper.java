package com.catl.cal_index.mapper.calIndex;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catl.cal_index.entity.calIndex.CalIndexType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CalIndexTypeMapper extends BaseMapper<CalIndexType> {
    /**
     *  获取所有的指标类型
     * @return
     */
    List<CalIndexType> getAllCalIndexType();

    /**
     *  根据指标类型唯一表示 calIndex, 查询具体指标
     * @param calIndex
     * @return
     */
    CalIndexType getTypeByCalIndex(String calIndex);
}
