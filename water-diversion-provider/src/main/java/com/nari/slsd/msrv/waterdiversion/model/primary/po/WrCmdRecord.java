package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("WR_CMD_RECORD")
public class WrCmdRecord {
    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    /**
     * 调度指令id
     */
    @TableField(value = "CMD_ID")
    private String cmdId;

    /**
     * 执行操作
     */
    @TableField(value = "EXECUTE_CONTENT")
    private String executeContent;

    /**
     * 操作人
     */
    @TableField(value = "OPERATOR")
    private String operator;

    /**
     * 操作时间
     */
    @TableField(value = "OPERATE_TIME")
    private Date operatorTime;

}
