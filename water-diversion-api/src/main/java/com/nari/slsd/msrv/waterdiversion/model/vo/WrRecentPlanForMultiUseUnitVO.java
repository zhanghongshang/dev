package com.nari.slsd.msrv.waterdiversion.model.vo;

import com.nari.slsd.msrv.waterdiversion.model.dto.SimpleWrBuildingAndDiversion;
import lombok.Data;

import java.util.List;

@Data
public class WrRecentPlanForMultiUseUnitVO {
    /**
     * 借调方用水单位下辖所有引水口
     */
    private List<SimpleWrBuildingAndDiversion> lendInBuildingList;
    /**
     * 借出方用水单位下辖所有引水口
     */
    private List<SimpleWrBuildingAndDiversion> lendOutBuildingList;

}
