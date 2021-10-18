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
    private static final String DATE_TIME_FIELD = "createTime";

    private static final String DATE_TIME_UPDATE_FIELD = "updateTime";

    private static final String ACTIVE_FIELD = "activeFlag";

    /**
     * 插入时的填充策略
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill...");
        if (metaObject.hasSetter(DATE_TIME_FIELD)) {
            this.strictFillStrategy(metaObject,DATE_TIME_FIELD, Date::new);
        }else if (metaObject.hasSetter(ACTIVE_FIELD)) {
            this.strictFillStrategy(metaObject,ACTIVE_FIELD, () -> Integer.valueOf(1));
        }
        log.info("end insert fill...");
    }

    /**
     * 更新时的填充策略
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill...");
        if (metaObject.hasSetter(DATE_TIME_UPDATE_FIELD)) {
            this.setFieldValByName(DATE_TIME_UPDATE_FIELD, new Date(), metaObject);
        }
    }
}
