package com.nari.slsd.msrv.waterdiversion.cache.interfaces;


import com.nari.slsd.msrv.waterdiversion.model.vo.DeptResponse;
import com.nari.slsd.msrv.waterdiversion.model.vo.UserResponse;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;

import java.util.List;

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
     * 增加用水单位树缓存
     *
     * @param nodeId
     * @param node
     * @return
     */
    Boolean setWaterUseUnitTree(String nodeId, WrUseUnitNode node);

    /**
     * 修改用水单位树缓存
     *
     * @param nodeId
     * @param node
     * @return
     */
    Boolean updateWaterUseUnitTree(String nodeId, WrUseUnitNode node);

    /**
     * 获取用水单位下的模型树
     *
     * @param nodeId
     * @return
     */
    WrUseUnitNode getWaterUseUnitTree(String nodeId);

    /**
     * 删除用水单位对应的模型树
     *
     * @param nodeId
     * @return
     */
    Boolean delWaterUseUnitTree(String nodeId);

    /**
     * 获取多个用水单位下的模型树
     *
     * @param nodeIds
     * @return
     */
    List<WrUseUnitNode> getWaterUseUnitTreeList(List<String> nodeIds);

    /**
     * 增加管理单位hash缓存
     *
     * @param id
     * @param vo
     */
    void setMngUnit(String id, DeptResponse vo);

    /**
     * 修改管理单位缓存
     *
     * @param id
     * @param vo
     */
    void updateMngUnit(String id, DeptResponse vo);

    /**
     * 获取管理单位
     *
     * @param id
     * @return
     */
    DeptResponse getMngUnit(String id);

    /**
     * 获取管理名称
     *
     * @param id
     * @return
     */
    String getMngUnitName(String id);

    /**
     * 删除管理单位
     *
     * @param id
     */
    void delMngUnit(String id);

    /**
     * 增加用户hash缓存
     *
     * @param id
     * @param vo
     */
    void setUser(String id, UserResponse vo);

    /**
     * 修改用户缓存
     *
     * @param id
     * @param vo
     */
    void updateUser(String id, UserResponse vo);

    /**
     * 获取用户
     *
     * @param id
     * @return
     */
    UserResponse getUser(String id);

    /**
     * 获取用户名称
     *
     * @param id
     * @return
     */
    String getUserName(String id);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    void delUser(String id);
    /**
     * 获取用户真实姓名
     */
    String getRealName(String id);
    /**
     * TODO 增加测站-引水口的redis实现
     */
}
