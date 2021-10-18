package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * @Program: water-diversion
 * @Description: 测站-引水口枚举类
 * @Author: reset kalar
 * @Date: 2021-08-09 17:22
 **/
public class WrBuildingEnum {
    /**
     * 测站-引水口类型
     * //全部测站类型
     * stationType: [
     * { name: "节制",id: "100001" },
     * { name: "分水",id: "100002" },
     * { name: "退水",id: "100003" },
     * { name: "节制-分水闸站",id: "100004" },
     * { name: "泵组",id: "100005" }，
     * { name: "水情测站",id: "100007" }
     * ]，
     * //填报引水口类型
     * waterIntakeType: [
     * { name: "节制",id: "100001" },
     * { name: "分水",id: "100002" },
     * { name: "退水",id: "100003" },
     * { name: "节制-分水闸站",id: "100004" },
     * { name: "泵组",id: "100005" }，
     * { name: "涵管",id: "100006" }
     * ]
     */
    public static final String BUILDING_TYPE_JZ = "100001";
    public static final String BUILDING_TYPE_FS = "100002";
    public static final String BUILDING_TYPE_TS = "100003";
    public static final String BUILDING_TYPE_JZ_FS = "100004";
    public static final String BUILDING_TYPE_BZ = "100005";
    public static final String BUILDING_TYPE_HG = "100006";
    public static final String BUILDING_TYPE_SQ = "100007";
    /**
     * 采集方式
     * 1 自动采集
     * 0 人工采集
     */
    public static final Integer COLLECT_TYPE_AUTO = 1;
    public static final Integer COLLECT_TYPE_MANUAL = 0;

    /**
     * 站点属性
     * 0 水情点
     * 1 工情点
     */
    public static final Integer SITE_TYPE_SQ = 0;
    public static final Integer SITE_TYPE_GQ = 1;

    /**
     * 是否填报
     * 1 填报
     * 0 不填报
     */
    public static final Integer IF_FILL_REPORT_TRUE = 1;
    public static final Integer IF_FILL_REPORT_FALSE = 0;

    /**
     * 引水口层级
     * 填报相关的传23
     *
     * 1 一级
     * 2 二级 二级引水口上级只能是一级引水口
     * 3 一二级共用
     */
    public static final Integer BUILDING_LEVEL_1 = 1;
    public static final Integer BUILDING_LEVEL_2 = 2;
    public static final Integer BUILDING_LEVEL_1_2 = 3;


}
