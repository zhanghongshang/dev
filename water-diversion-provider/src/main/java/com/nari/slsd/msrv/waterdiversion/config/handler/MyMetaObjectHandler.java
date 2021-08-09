package com.nari.slsd.msrv.waterdiversion.config.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Program: water-diversion
 * @Description: insert和update的默认填充策略
 * @Author: reset kalar
 * @Date: 2021-07-29 14:52
 **/

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    //插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill...");
//        this.setFieldValByName("date", new Date(), metaObject);

    }

    //更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill...");
//        this.setFieldValByName("date", new Date(), metaObject);
    }
}
