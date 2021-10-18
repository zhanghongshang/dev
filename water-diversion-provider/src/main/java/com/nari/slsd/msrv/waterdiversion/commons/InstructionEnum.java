package com.nari.slsd.msrv.waterdiversion.commons;

/**
 * 指令相关
 *
 * @author bigb
 */

public class InstructionEnum {
    
    /**
     * ========指令状态类型=======
     */

    /**
     * 取消（包括重算取消
     */
    //public static final String CANCEL = "0";

    /**
     * 待审批（修改该状态为待执行）
     */
    //public static final String PENDING_APPROVE = "1";

    /**
     * 已审批
     */
    //public static final String APPROVED = "2";

    /**
     * 无需审批，等待下发
     */
    //public static final String PENDING_SEND = "3";

    /**
     * 已下发
     */
    //public static final String SENDED = "4";

    /**
     * 已完成
     */
    //public static final String FINISHED = "5";
    /**
     * 待调度下发
     */
    public static final String PENDING_DISPATCH_ISSUE = "0";
    /**
     * 待辅助确认
     */
    public static final String PENDING_ASSIST_CONFIRM = "1";
    /**
     * 调度已下发,待段长下发
     */
    public static final String PENDING_SEGMENT_ISSUE = "2";
    /**
     * 段长已下发,待执行
     */
    public static final String PENDING_EXECUTE = "3";
    /**
     * 指令执行中
     */
    public static final String EXECUTING = "4";
    /**
     * 指令执行完成
     */
    public static final String FINISHED = "5";

    /**
     * ========指令操作类型=======
     */

    /**
     * 指令下发(调度/站长下发)
     */
    public static final String OPERATE_DISPATCH_SEND = "0";

    /**
     * 指令下发（段长）
     */
    public static final String OPERATE_SEGMENT_SEND = "1";

    /**
     * 辅助确认
     */
    public static final String OPERATE_ASSIST_CONFIRM = "2";

    /**
     * 指令执行
     */
    public static final String OPERATE_EXECUTE = "3";

    /**
     * 执行完成
     */
    public static final String OPERATE_EXECUTE_FINISHED = "4";

    /**
     * ========调度类型=======
     */

    /**
     * 人工调度
     */
    public static final String SCHEDULE_MANUAL = "0";

    /**
     * 计划调度
     */
    public static final String SCHEDULE_PLAN = "1";

    /**
     * 指令模板
     */
    public static final String ORDER_CONTENT_TEMPLATE = "根据开都-孔雀河流域综合调度需要，请{0}于{1}至{2}将{3}流量由{4}变更为{5}立方米每秒。";

}

