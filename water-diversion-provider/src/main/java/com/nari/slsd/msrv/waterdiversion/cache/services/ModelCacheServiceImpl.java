package com.nari.slsd.msrv.waterdiversion.cache.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.RedisCacheKeyDef;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.config.RedisUtil;
import com.nari.slsd.msrv.waterdiversion.model.vo.DeptResponse;
import com.nari.slsd.msrv.waterdiversion.model.vo.UserResponse;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append(nodeId).toString();
        String value = JSON.toJSONString(node);
        boolean result = redisUtil.set(key, value);
        return result;
    }

    @Override
    public Boolean updateWaterUseUnitTree(String nodeId, WrUseUnitNode node) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append(nodeId).toString();
        String value = JSON.toJSONString(node);
        boolean result = redisUtil.set(key, value);
        return result;
    }

    @Override
    public WrUseUnitNode getWaterUseUnitTree(String nodeId) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append(nodeId).toString();
        Object node = redisUtil.get(key);
        if (node == null) {
            return null;
        }
        WrUseUnitNode jsonObject = JSON.toJavaObject(JSON.parseObject(node.toString()), WrUseUnitNode.class);
        return jsonObject;
    }

    @Override
    public Boolean delWaterUseUnitTree(String nodeId) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.TREE).append(nodeId).toString();
        redisUtil.del(key);
        return null;
    }

    @Override
    public List<WrUseUnitNode> getWaterUseUnitTreeList(List<String> nodeIds) {
        List<WrUseUnitNode> nodeList = new ArrayList<>();
        nodeIds.forEach(id -> {
            WrUseUnitNode node = getWaterUseUnitTree(id);
            if (node != null) {
                nodeList.add(node);
            }
        });
        return nodeList;
    }


    @Override
    public void setMngUnit(String id, DeptResponse vo) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.DEPT).append(id).toString();
        String value = JSON.toJSONString(vo);
        redisUtil.set(key, value);
    }

    @Override
    public void updateMngUnit(String id, DeptResponse vo) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.DEPT).append(id).toString();
        String value = JSON.toJSONString(vo);
        redisUtil.set(key, value);
    }

    @Override
    public DeptResponse getMngUnit(String id) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.DEPT).append(id).toString();
        String str = redisUtil.getStr(key);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSON.toJavaObject(JSONObject.parseObject(str), DeptResponse.class);
    }

    @Override
    public String getMngUnitName(String id) {
        DeptResponse response = getMngUnit(id);
        return response == null ? null : response.getName();
    }


    @Override
    public void delMngUnit(String id) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.DEPT).append(id).toString();
        redisUtil.del(key);
    }

    @Override
    public void setUser(String id, UserResponse vo) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.USER).append(id).toString();
        String value = JSON.toJSONString(vo);
        redisUtil.set(key, value);
    }

    @Override
    public void updateUser(String id, UserResponse vo) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.USER).append(id).toString();
        String value = JSON.toJSONString(vo);
        redisUtil.set(key, value);
    }

    @Override
    public UserResponse getUser(String id) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.USER).append(id).toString();
        String str = redisUtil.getStr(key);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSON.toJavaObject(JSONObject.parseObject(str), UserResponse.class);
    }

    @Override
    public String getUserName(String id) {
        UserResponse response = getUser(id);
        return response == null ? null : response.getUsername();
    }

    @Override
    public void delUser(String id) {
        String key = new StringBuffer(RedisCacheKeyDef.ModelKey.USER).append(id).toString();
        redisUtil.del(key);
    }

    /**
     *  userId获取用户真实姓名
     * @param id
     * @return
     */
    @Override
    public String getRealName(String id) {
        UserResponse response = getUser(id);
        return response == null ? null : response.getRealname();
    }

}
