package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * 近期计划调整
 *
 * @author bigb
 */

public class RecentPlanEnum {
    
    /**
     * ========调整类型=======
     * 0-2 是同一个用水单位下（县市师团级用水单位）
     */

    /**
     * 本水口月内调整
     */
    public static final String BUILDING_IN_MONTH = "0";

    /**
     * 本月跨引水口
     */
    public static final String OTHER_BUILDING_IN_MONTH = "1";

    /**
     * 本引水口跨月
     */
    public static final String BUILDING_IN_OTHER_MONTH = "2";

    /**
     * 跨用水单位调整
     */
    public static final String BUILDING_IN_OTHER_WATER_UNIT = "3";

    /**
     * 借调方
     */
    public static final String LEND_IN = "0";

    /**
     * 借出方
     */
    public static final String LEND_OUT = "1";

    /**
     * 借调来源-结余
     */
    public static final String LEND_OUT_SOURCE_REMAIN_PAST = "月结余水量借调";

    /**
     * 借调来源-月指标
     */
    public static final String LEND_OUT_SOURCE_REMAIN_OF_MONTH = "月剩余指标借调";

    /**
     * 借调来源-年指标
     */
    public static final String LEND_OUT_SOURCE_REMAIN_OF_YEAR = "年剩余指标借调";


}

