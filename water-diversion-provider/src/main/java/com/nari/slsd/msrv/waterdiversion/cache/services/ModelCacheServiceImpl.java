package com.nari.slsd.msrv.waterdiversion.cache.services;

import com.alibaba.fastjson.JSON;
import com.nari.slsd.msrv.waterdiversion.cache.RedisCacheKeyDef;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.config.RedisUtil;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 缓存处理实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */

@Service("modelCacheService")
public class ModelCacheServiceImpl implements IModelCacheService {

    @Resource
    RedisUtil redisUtil;


    @Override
    public Boolean setWaterUseUnitTree(String nodeId, WrUseUnitNode node) {
        String key =new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append (nodeId).toString ();
        String value = JSON.toJSONString (node);
        boolean result = redisUtil.set(key,value);
        return result;
    }

    @Override
    public Boolean updateWaterUseUnitTree(String nodeId, WrUseUnitNode node) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append (nodeId).toString ();
        String value = JSON.toJSONString (node);
        boolean result = redisUtil.set(key,value);
        return result;
    }

    @Override
    public WrUseUnitNode getWaterUseUnitTree(String nodeId) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append (nodeId).toString ();
        Object node = redisUtil.get(key);
        if (node == null) {
            return null;
        }
        WrUseUnitNode jsonObject = JSON.toJavaObject (JSON.parseObject (node.toString ()), WrUseUnitNode.class);
        return jsonObject;
    }

    @Override
    public Boolean delWaterUseUnitTree(String nodeId) {
        String key =new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append (nodeId).toString ();
        redisUtil.del(key);
        return null;
    }


}
