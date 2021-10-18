package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description 计划水引数据对比
 * @Author ZHS
 * @Date 2021/9/10 10:50
 */
@Data
public class WrPlanDataContrast {

    //引水口id
    private String buildingId;
    //年计划与实际引水量对比值
    private PlanContrast planAndActContrast;
    //管理站id
    private String mngUnitId;

}
