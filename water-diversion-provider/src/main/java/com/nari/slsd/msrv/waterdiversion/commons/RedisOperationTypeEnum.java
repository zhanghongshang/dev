package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * redis相关
 *
 * @author bigb
 */

public class RedisOperationTypeEnum {
    
    

    /**
     * ========值类型=======
     */

    /**
     * hash
     */
    public static final String HASH = "hash";

    /**
     * json
     */
    public static final String JSON = "json";

    /**
     * ========调度模式=======
     */

    /**
     * 计划模式
     */
    public static final String PLAN = "JH";

    /**
     * 人工模式
     */
    public static final String MANUAL = "RG";

    /**
     * 费率管理
     */
    public static final String FEE_RATE = "FL";

    /**
     * 收费管理
     */
    public static final String WATER_CHARGE = "SF";

    /**
     * 调度指令管理
     */
    public static final String WATER_CMD_MANAGER = "CM";

    /**
     * 调度指令管理-实时调度指令
     */
    public static final String WATER_CMD_MANAGER_REAL = "CM_REAL";

    /**
     * 水权交易
     */
    public static final String RIGHT_TRADE = "SQJY";

}

