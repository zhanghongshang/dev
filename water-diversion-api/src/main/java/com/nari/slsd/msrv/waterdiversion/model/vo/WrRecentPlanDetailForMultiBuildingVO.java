package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class WrRecentPlanDetailForMultiBuildingVO {

    /**
     * 引水口id
     */
    private String buildingId;
    /**
     * 引水口名称
     */
    private String buildingName;
    /**
     * 借调方表头
     */
    private List<String> lendInHeadList;
    /**
     * 借调方
     */
    private List<WrPlanFillinDayVO> lendInVOList;

}
