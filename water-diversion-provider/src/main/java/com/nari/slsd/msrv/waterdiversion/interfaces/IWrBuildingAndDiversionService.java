package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;

/**
 * <p>
 * 测站-引水口管理 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
public interface IWrBuildingAndDiversionService {

    /**
     * 分页查询
     *
     * @param start
     * @param length
     * @param buildingType
     * @param waterUnitId
     * @param mngUnitId
     * @return
     */
    DataTableVO getBuildingAndDiversionPage(Integer start, Integer length, Integer buildingType, String waterUnitId, String mngUnitId);


    /**
     * 编辑更新
     *
     * @param dto
     */
    void updateBuildingAndDiversion(WrBuildingAndDiversion dto);
}
