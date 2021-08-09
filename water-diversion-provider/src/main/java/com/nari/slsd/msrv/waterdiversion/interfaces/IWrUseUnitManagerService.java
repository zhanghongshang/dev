package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrUseUnitManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.po.WrUseUnitManager;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;

import java.util.List;

/**
 * <p>
 * 用水单位管理 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
public interface IWrUseUnitManagerService extends IService<WrUseUnitManager> {

    /**
     * 新增用水单位
     *
     * @param dto
     */
    void addWaterUseUnit(WrUseUnitManagerDTO dto);

    /**
     * 修改用水单位信息
     *
     * @param dto
     */
    void updateWaterUseUnit(WrUseUnitManagerDTO dto);

    /**
     * 删除用水单位
     *
     * @param id
     */
    void deleteWaterUseUnitById(String id);

    /**
     * 查询全部用水单位信息
     *
     * @param
     * @return
     */
    List<WrUseUnitManagerVO> getAllWaterUseUnitList();


    /**
     * 按条件分页查询用水单位信息
     *
     * @param start  页码
     * @param length 页面长度
     * @param pid    上级用水单位ID
     * @param state  状态
     * @return
     */
    DataTableVO getWaterUseUnitPage(Integer start, Integer length, String pid, Integer state);


    /**
     * 从redis缓存获取模型树
     *
     * @param ids
     * @return
     */
    List<WrUseUnitNode> getTreeFromCacheByIds(List<String> ids);

    /**
     * 从redis缓存获取全部模型树
     *
     * @return
     */
    List<WrUseUnitNode> getAllTreeFromCacheByIds();

    /**
     * 获取子节点ID
     *
     * @param pid 父节点ID
     * @return
     */
    List<String> getUnitIdsByPid(String pid);


    /**
     * 从redis缓存获取模型树
     *
     * @param id
     * @return
     */
    WrUseUnitNode getTreeFromCacheById(String id);


    /**
     * 校验code唯一性
     *
     * @param code
     * @return
     */
    Boolean checkUniqueCode(String code);
}
