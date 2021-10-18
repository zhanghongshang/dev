package com.nari.slsd.msrv.waterdiversion.commons;
/**
 * @Description 工作流相关枚举
 * @Author ZHS
 * @Date 2021/9/16 19:24
 */
public enum ActivtciEnum {
    FLAG_ADOPT_ONE("1", " 通过网关1"),
    FLAG_ADOPT_TWO("2", "通过网关2");

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

    ActivtciEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    ActivtciEnum getEnumById(String id) {
        for (ActivtciEnum e : values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
