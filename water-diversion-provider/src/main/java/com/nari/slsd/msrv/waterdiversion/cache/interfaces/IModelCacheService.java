package com.nari.slsd.msrv.waterdiversion.cache.interfaces;


import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;

/**
 * <p>
 * 缓存处理类
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */

public interface IModelCacheService {

    /**
     * 增加用户单位树缓存
     *
     * @param nodeId
     * @param node
     * @return
     */
    Boolean setWaterUseUnitTree(String nodeId, WrUseUnitNode node);

    /**
     * 修改用户单位树缓存
     *
     * @param nodeId
     * @param node
     * @return
     */
    Boolean updateWaterUseUnitTree(String nodeId, WrUseUnitNode node);

    /**
     * 获取用户单位下的模型树
     *
     * @param nodeId
     * @return
     */
    WrUseUnitNode getWaterUseUnitTree(String nodeId);

    /**
     * 删除用户单位对应的模型树
     *
     * @param nodeId
     * @return
     */
    Boolean delWaterUseUnitTree(String nodeId);


}
