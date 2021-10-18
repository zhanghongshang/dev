package com.nari.slsd.msrv.waterdiversion.init;

import com.nari.slsd.msrv.waterdiversion.init.interfaces.IInitModelCache;
import com.nari.slsd.msrv.waterdiversion.utils.ApplicationContextUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @program: msrv-project
 * @description: 消费者初始化
 * @author: Created by ZHD
 * @create: 2020-08-14 11:33
 **/

public class RedisListenerInit implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if("bootstrap".equals(event.getApplicationContext().getParent().getId())){
            //初始化缓存
            IInitModelCache modelCache = (IInitModelCache)event.getApplicationContext().getBean("initModelCacheImpl");
            modelCache.initWaterUseUnitTree ();
        }
        ApplicationContextUtils.setContext(event.getApplicationContext());
    }
}
