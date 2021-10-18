package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * @description: 用水计划
 * @author: Created by ZHD
 * @date: 2021/8/16 15:40
 * @return:
 */
public enum PlanTypeEnum {

    YEAR_PLAN("0", "年计划"),
    MONTH_PLAN("1", "月计划"),
    RECENT_PLAN("2", "近期计划"),
    YEAR_PLAN_FULL("3", "全量年计划"),
    MONTH_PLAN_DAY("1", "月计划日类别"),
    MONTH_PLAN_TDAY("0", "月计划旬类别"),
    UNIT_TYPE_MANAGER("0", "管理单位"),
    UNIT_TYPE_USE("1", "用水单位");

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

    PlanTypeEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    PlanTypeEnum getEnumById(String id) {
        for (PlanTypeEnum e : values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
