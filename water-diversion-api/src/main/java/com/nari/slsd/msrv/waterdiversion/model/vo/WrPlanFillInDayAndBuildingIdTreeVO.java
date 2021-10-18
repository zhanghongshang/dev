package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class WrPlanFillInDayAndBuildingIdTreeVO {
    //借调或借出查询
    private List<WrPlanFillinDayVO> wrPlanFillinDayVO;
    //引水口对应月剩余水量
    private List<SimpleWrBuildingVO> simpleWrBuildingVOS;
}
