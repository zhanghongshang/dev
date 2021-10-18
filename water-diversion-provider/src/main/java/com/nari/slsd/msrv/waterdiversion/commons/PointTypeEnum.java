package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * @description: 测点类型枚举
 * @author: Created by ZHD
 * @date: 2021/8/16 15:40
 * @return:
 */
public enum PointTypeEnum {

    WATER_VOLUME("1", "实时水量"),
    FLOW("2", "实时流量"),
    OPENING("3", "实时开度"),
    WATER_LEVEL("8", "水位");//闸后实时水位

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

    PointTypeEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    PointTypeEnum getEnumById(String id) {
        for (PointTypeEnum e : values()
                ) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
