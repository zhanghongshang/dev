package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class WrRecentPlanDetailVO {

    /**
     * 流程id
     */
    private String processId;
    /**
     * 流程节点标识
     */
    private String nodeFlag;
    /**
     * 子类型
     */
    private String subType;
    /**
     * 子类型
     */
    private String batchState;
    /**
     * 借调方表头
     */
    private List<String> lendInHeadList;
    /**
     * 借出方表头
     */
    private List<String> lendOutHeadList;
    /**
     * 借调方
     */
    private List<WrPlanFillinDayVO> lendInVOList;
    /**
     * 借出方
     */
    private List<WrPlanFillinDayVO> lendOutVOList;

    private List<WrRecentPlanDetailForMultiBuildingVO> lendInVOListForMultiUseUnit;

}
