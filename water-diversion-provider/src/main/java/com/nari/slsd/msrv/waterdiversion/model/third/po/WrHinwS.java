package com.nari.slsd.msrv.waterdiversion.model.third.po;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * 地表水取水口实时监测表
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Data
@TableName(value = "WR_HINW_S", schema = "USER_WR")
public class WrHinwS implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地表水取水口代码
     * 主键
     */
    @TableField("SWFCD")
    private String swfcd;

    /**
     * 测定时间
     * 主键
     */
    @TableField(value = "MNTM")
    private Date mntm;

    /**
     * 水位
     */
    @TableField("Z")
    private Double z;

    /**
     * 流量
     */
    @TableField("Q")
    private Double q;

    /**
     * 累计引水量
     */
    @TableField("ACCPW")
    private Double accpw;

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
     * 审核时间
     */
    @TableField("ETM")
    private Date etm;

    /**
     * 审核人
     */
    @TableField("MDPC")
    private String mdpc;


}
