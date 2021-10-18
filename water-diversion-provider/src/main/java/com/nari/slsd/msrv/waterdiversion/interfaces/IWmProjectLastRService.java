package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.nari.slsd.msrv.waterdiversion.model.fourth.po.WmProjectLastR;
import com.nari.slsd.msrv.waterdiversion.model.vo.BuildingExt;

import java.util.List;

/**
 * <p>
 * 测站采集最新水情表 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
public interface IWmProjectLastRService extends IMppService<WmProjectLastR> {


    /**
     * 根据管理单位多条件查询测站最新水位流量数据
     *
     * @param mngUnitIds     管理单位ID
     * @param unitLevels     用水单位层级
     * @param buildingTypes  测站类型
     * @param fillReport     是否填报
     * @param buildingLevels 引水口层级
     * @return
     */
    List<BuildingExt> getNewestDataWithBuildingExt(List<String> mngUnitIds, List<Integer> unitLevels, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels);

    /**
     * 根据测站编码查询最新数据
     *
     * @param stcds 测站编码
     * @return 数据poList
     */
    List<WmProjectLastR> getNewestData(List<String> stcds);

}
