package com.nari.slsd.msrv.waterdiversion.commons;
/**
 * @Description 旬数据日数据调整枚举
 * @Author ZHS
 * @Date 2021/9/23 18:10
 */
public enum WrPlanInterEnum {
    PLNA_INTER_DAY("0", " 日迭代"),
    PLNA_INTER_TDAY("1", "旬迭代"),

    PLNA_ADJUST_NOT_EXCEED("0", "差值百分比不超20%"),
    PLNA_ADJUST_EXCEED("1", " 差值百分比超20%");


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

    WrPlanInterEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    WrPlanInterEnum getEnumById(String id) {
        for (WrPlanInterEnum e : values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
