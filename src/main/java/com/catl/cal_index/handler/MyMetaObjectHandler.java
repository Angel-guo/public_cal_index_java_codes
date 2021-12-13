package com.catl.cal_index.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    private static final Logger logger =  LoggerFactory.getLogger(MyMetaObjectHandler.class);

    /**
     * 当mysql中插入一条数据时，就自动创建对应的 create_time
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        logger.info("start insert fill ....");

        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
    }

    /**
     * 当mysql中更新一条数据时，就自动创建对应的 update_time
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        logger.info("start update fill ....");

        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
    }
}