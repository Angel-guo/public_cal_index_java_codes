package com.catl.cal_index.mapper.calIndex;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catl.cal_index.entity.calIndex.VinMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component(value = "vinMapperMapper")
public interface VinMapperMapper extends BaseMapper<VinMapper> {
    /**
     * 根据 明文或者密文，查询明密文
     * @param vin
     * @return
     */
    VinMapper getVinOrMvinByVin(@Param("vin")String vin);
}
