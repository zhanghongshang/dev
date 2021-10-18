package com.nari.slsd.msrv.waterdiversion.commons;
/**
 * @Description 任务状态枚举
 * @Author ZHS
 * @Date 2021/9/1 10:21
 */
public enum TaskStateEnum {

    UNDER_APPROVAL("0", "审批中"),
    END_APPROVAL("1", "审批结束"),
    NO_APPROVAL("2", "已拒绝");



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

    TaskStateEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    TaskStateEnum getEnumById(String id) {
        for (TaskStateEnum e : values()
        ) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

}
