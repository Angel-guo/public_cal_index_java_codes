package com.catl.cal_index.mapper.calIndex;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catl.cal_index.entity.calIndex.CalIndexRecord;
import com.catl.cal_index.entity.calIndex.CarBatteryMap;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author daiji
 * @since 2021-11-08
 */
@Component
public interface CarBatteryMapMapper extends BaseMapper<CarBatteryMap> {

    /**
     * 获取所有的电池型号列表
     * @return
     */
    List<CarBatteryMap> getBatteryList();

    /**
     * 根据电池型号获取车企列表
     * @param batteryModel
     * @return
     */
    List<CarBatteryMap> getAutomobileEnterpriseByBattery(@Param("battery") String batteryModel);
}
