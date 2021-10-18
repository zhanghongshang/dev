package com.nari.slsd.msrv.waterdiversion.commons;
/**
 * @Description 计划填报相关枚举
 * @Author ZHS
 * @Date 2021/9/8 16:48
 */
public enum PlanFillInTypeEnum {
    YEAR_PLAN_FILL_IN("0", "年度用水计划填报"),
    MONTH_PLAN_FILL_IN("1", "月度用水计划填报"),
    DAY_PLAN_FILL_IN("2", "近期用水计划调整"),

    DAY_PLAN_FILL_IN_WAM("0", "近期用水计划调整(月内)"),
    DAY_PLAN_FILL_IN_TLI("1", "近期用水计划调整(跨月)"),
    DAY_PLAN_FILL_IN_SUP("2", "近期用水计划调整(超年)"),

    MNG_UNIT("0", "管理单位"),
    WATER_UNIT("1", "用水单位");

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    PlanFillInTypeEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    PlanFillInTypeEnum getEnumById(String id) {
        for (PlanFillInTypeEnum e : values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
