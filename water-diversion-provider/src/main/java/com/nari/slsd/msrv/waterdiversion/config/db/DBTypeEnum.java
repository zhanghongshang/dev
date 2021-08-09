package com.nari.slsd.msrv.waterdiversion.config.db;

public enum DBTypeEnum {
    db1("dm-promng"), db2("db2");
    private String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
