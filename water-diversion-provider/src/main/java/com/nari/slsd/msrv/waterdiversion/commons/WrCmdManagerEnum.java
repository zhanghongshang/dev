package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * 调度指令管理相关
 *
 * @author bigb
 */

public class WrCmdManagerEnum {
    
    

    /**
     * ========调度指令状态类型=======
     */

    /**
     * 待审批
     */
    public static final String PENDING_APPROVE = "0";

    /**
     * 审批通过
     */
    public static final String APPROVED = "1";

    /**
     * 审批不通过
     */
    public static final String NO_APPROVED = "2";


    /**
     * ========调令类型=======
     */

    /**
     * 计划指令
     */
    public static final String PLAN_ORDER = "1";

    /**
     * 人工指令
     */
    public static final String MANUAL_ORDER = "0";

    /**
     * ========操作类型=======
     */

    /**
     * 修改
     */
    public static final String UPDATE = "1";

    /**
     * 审批
     */
    public static final String APPROVE = "2";

}

