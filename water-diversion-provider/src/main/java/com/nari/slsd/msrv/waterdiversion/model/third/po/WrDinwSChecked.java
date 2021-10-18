package com.nari.slsd.msrv.waterdiversion.model.third.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>
 * 地表水取水口日引水监测表（已审批日监测数据表）
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Data
@TableName(value = "WR_DINW_S_CHECKED",schema = "USER_WR")
public class WrDinwSChecked implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地表水取水口代码
     * 主键
     */
    @TableField(value = "SWFCD")
    private String swfcd;

    /**
     * 测定时间
     * 主键
     */
    @TableField("MNTM")
    private Date mntm;

    /**
     * 8时流量
     */
    @TableField("EQ")
    private Double eq;

    /**
     * 日均流量
     */
    @TableField("DVQ")
    private Double dvq;

    /**
     * 最大流量
     */
    @TableField("MAQ")
    private Double maq;

    /**
     * 最小流量
     */
    @TableField("MIQ")
    private Double miq;

    /**
     * 日累计引水量（含公摊水量）
     */
    @TableField("FWQT")
    private Double fwqt;

    /**
     * 水质类别
     */
    @TableField("WQG")
    private String wqg;

    /**
     * 备注
     */
    @TableField("NT")
    private String nt;

    /**
     * 机构代码
     */
    @TableField("GDWR_OGID")
    private String gdwrOgid;

    /**
     * 交换标示位
     */
    @TableField("GDWR_SDFL")
    private String gdwrSdfl;

    /**
     * 修改人
     */
    @TableField("GDWR_MDPS")
    private String gdwrMdps;

    /**
     * 修改日期
     */
    @TableField("GDWR_MDDT")
    private Date gdwrMddt;

    /**
     * 数据来源
     */
    @TableField("GDWR_DASC")
    private String gdwrDasc;

    /**
     * 数据说明
     */
    @TableField("GDWR_DSPT")
    private String gdwrDspt;

    /**
     * 数据标识
     */
    @TableField("GDWR_DAFL")
    private String gdwrDafl;

    /**
     * 数据流水号
     */
    @TableField("GDWR_ATID")
    private String gdwrAtid;

    /**
     * 最大流量出现时间
     */
    @TableField("MAQT")
    private Date maqt;

    /**
     * 最小流量出现时间
     */
    @TableField("MIQT")
    private Date miqt;

    /**
     * 审核时间
     */
    @TableField("ETM")
    private Date etm;

    /**
     * 审核人
     */
    @TableField("MDPC")
    private String mdpc;

    /**
     * 日均水位
     */
    @TableField("AVZ")
    private Double avz;

    /**
     * 采集方式.AT:自动,MT:人工录入
     */
    @TableField("ATMT")
    private String atmt;

    /**
     * 审核标识.Y:已审核,N:未审核
     */
    @TableField("CHECKED")
    private String checked;

    /**
     * 日累计引水量（不含公摊水量的引水口实测水量）
     */
    @TableField("FWQTT")
    private Double fwqtt;

    /**
     * 审核备注
     */
    @TableField("CKNT")
    private String cknt;

    /**
     * 8时水位
     */
    @TableField("EZ")
    private Double ez;

    /**
     * 审核人登录名
     */
    @TableField("MDPCD")
    private String mdpcd;


}
