package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 水工建筑物管理
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrPlanFillinDayAdjustVO {
    /**
     * 调整类型（0：跨月 1：跨年 2：超年）
     */
    private String adjustType;

    //近期填报类(跨月,日迭代)
    private List<WrPlanFillinDayVO> wrPlanFillinDay;
    //旬月迭代(跨月,旬月迭代)
    private List<WrPlanFillinDayVO> wrPlanFillinTDay;

}
