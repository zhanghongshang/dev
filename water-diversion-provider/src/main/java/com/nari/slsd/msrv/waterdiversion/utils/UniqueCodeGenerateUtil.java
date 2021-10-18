package com.nari.slsd.msrv.waterdiversion.utils;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName UniqueCodeGenerateUtil
 * @Description 使用redis生成唯一编码工具类
 * @createTime 2021年08月21日
 */
@Component
public class UniqueCodeGenerateUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * @title generateUniqueCode
     * @description
     * @author bigb
     * @param: key redis中的key
     * @param: prefix 编码前缀
     * @param: hasExpire 是否支持过期失效
     * @param: length 唯一编码部分长度
     * @updateTime 2021/8/21 7:17
     * @return: java.lang.String
     * @throws
     */
    public String generateUniqueCode(String key, String prefix, boolean hasExpire, Integer length){
        //支持过期失效
        Date expireTime = null;
        if(hasExpire){
            expireTime = DateUtil.endOfDay(DateUtil.date());
        }
        Long uniqueId = this.generateId(key, expireTime);
        StringBuilder sb = new StringBuilder(prefix);
        //获取当天日期
        sb.append(DateUtil.today().replaceAll("-",""));
        //长度不够，左补0
        String uniqueCode = StringUtils.leftPad(uniqueId.toString(),length,"0");
        sb.append(uniqueCode);
        return sb.toString();
    }

    /**
     * @title generateUniqueCode
     * @description
     * @author bigb
     * @param: key redis中的key
     * @param: hasExpire 是否支持过期失效
     * @param: length 唯一编码部分长度
     * @updateTime 2021/8/21 7:17
     * @return: java.lang.String
     * @throws
     */
    public String generateUniqueCode(String key, boolean hasExpire, Integer length){
        return generateUniqueCode(key,"",hasExpire,length);
    }

    /**
     * 获取递增id
     * @param key
     * @param date
     * @return
     */
    private Long generateId(String key, Date date) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        // 通过key获取自增并设定过期时间
        if(null != date){
            counter.expireAt(date);
        }
        //默认步长为1，后期需要再拓展
        return counter.incrementAndGet();
    }

}
