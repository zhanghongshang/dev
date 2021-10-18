package com.nari.slsd.msrv.waterdiversion.commons;


/**
 * @Program: water-diversion
 * @Description: 树模型枚举类
 * @Author: reset kalar
 * @Date: 2021-08-09 10:35
 **/
public class TreeEnum {
    /**
     * 用水单位第一级单位父节点ID -1
     */
    public static final String WR_USE_UNIT_ROOT_PID = "-1";

    /**
     * 节点类型
     * 1 管理单位
     * 2 用水单位
     * 3 测站引水口
     */
    public static final Integer NODE_TYPE_DEPT = 1;
    public static final Integer NODE_TYPE_UNIT = 2;
    public static final Integer NODE_TYPE_STATION = 3;
}
