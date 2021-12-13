package com.catl.cal_index.mapper.calIndex;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catl.cal_index.entity.calIndex.CalIndexRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author daiji
 * @since 2021-11-08
 */
@Component
public interface CalIndexRecordMapper extends BaseMapper<CalIndexRecord> {

    /**
     *  根据 calIndex 和 calIndexVars 获取当前任务执行状态
     * @param calIndex
     * @param calIndexVars
     * @return
     */
    CalIndexRecord getTaskStatusByCalIndex(@Param("calIndex") String calIndex, @Param("calIndexVars") String calIndexVars);

    /**
     * 根据 calIndex 和 calIndexVars 获取最近一次执行成功的结果
     * @param calIndex
     * @param calIndexVars
     * @return
     */
    CalIndexRecord getLatestSuccessRecord(@Param("calIndex") String calIndex, @Param("calIndexVars") String calIndexVars);

    /**
     * 根据 calIndex 和 calIndexVars 获取最近一次执行中的记录
     * @param calIndex
     * @param calIndexVars
     * @return
     */
    CalIndexRecord getLatestRunningRecord(@Param("calIndex") String calIndex, @Param("calIndexVars") String calIndexVars);

}
