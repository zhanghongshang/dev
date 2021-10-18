package com.nari.slsd.msrv.waterdiversion.config;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.nari.slsd.msrv.waterdiversion.cache.RedisCacheKeyDef;
import com.nari.slsd.msrv.waterdiversion.commons.RedisOperationTypeEnum;
import com.nari.slsd.msrv.waterdiversion.utils.UniqueCodeGenerateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName RedisUtilTest
 * @Description redis测试类
 * @createTime 2021年08月19日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisUtilTest {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UniqueCodeGenerateUtil uniqueCodeGenerateUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void mSetTest(){
        String keyPre = "mykey_";
        for(int i=0;i<5;i++){
            redisUtil.hset(keyPre+i, RedisCacheKeyDef.ModelKey.MNG_UNIT_ID,"id:"+i);
            redisUtil.hset(keyPre+i, RedisCacheKeyDef.ModelKey.MNG_UNIT_NAME,"mame:"+i);
        }
    }

    @Test
    public void mGetTestWithPipeline(){

    }

    @Test
    public void mGetTest(){

    }

    @Test
    public void mSetTest1(){
        String keyPre = "mykey111_";
        long start2 = System.currentTimeMillis();
        for(int i=0;i<5;i++){
            redisUtil.set(keyPre+i,i *10);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("==================================common=====================================cost:"+(end2-start2));
    }

    @Test
    public void mGetTest1(){
        String keyPre = "mykey111_";
        List<String> keyList = new ArrayList<>();
        for(int i=0;i<5;i++){
            keyList.add(keyPre+i);
        }
        long start2 = System.currentTimeMillis();
        redisUtil.multipleGet(keyList, RedisOperationTypeEnum.JSON, Map.class);
        long end2 = System.currentTimeMillis();
        System.out.println("==================================common=====================================cost:"+(end2-start2));
    }

    @Test
    public void generateUniqueCodeTest(){
        String key = RedisOperationTypeEnum.PLAN + DateUtil.today();
        String s = uniqueCodeGenerateUtil.generateUniqueCode(key, RedisOperationTypeEnum.PLAN, true, 6);
        System.out.println("======================"+s);
    }

}
