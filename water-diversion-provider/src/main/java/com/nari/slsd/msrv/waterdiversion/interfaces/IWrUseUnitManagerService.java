package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrUseUnitManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitManager;
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
    void saveWaterUseUnit(WrUseUnitManagerDTO dto);

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
     * 根据一批用水单位id获取用水单位信息
     *
     * @param   unitIds
     * @return
     */
    List<WrUseUnitManagerVO> getWaterUseUnitList(List<String> unitIds);


    /**
     * 按条件分页查询用水单位信息
     *
     * @param pageIndex  页码
     * @param pageSize 页面长度
     * @param pid    上级用水单位ID
     * @param state  状态
     * @return
     */
    DataTableVO getWaterUseUnitPage(Integer pageIndex, Integer pageSize, String pid, Integer state);


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
    List<WrUseUnitNode> getAllTreeFromCache();

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


    /**
     * 获取一级节点
     * @param po
     * @return
     */
    String getRootId(WrUseUnitManager po);

}
