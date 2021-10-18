package com.nari.slsd.msrv.waterdiversion.model.third.po;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * 地表水取水口月监测表
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Data
@TableName(value = "WR_MINW_S", schema = "USER_WR")
public class WrMinwS implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地表水取水口代码
     * 主键
     */
    @TableField("SWFCD")
    private String swfcd;

    /**
     * 测定年份
     * 主键
     */
    @TableField(value = "YR")
    private Integer yr;

    /**
     * 月份
     * 主键
     */
    @TableField("MNTH")
    private Integer mnth;

    /**
     * 月均流量
     */
    @TableField("TDEQ")
    private Double tdeq;

    /**
     * 月最大流量
     */
    @TableField("TDMAQ")
    private Double tdmaq;

    /**
     * 月最小流量
     */
    @TableField("TDMIQ")
    private Double tdmiq;

    /**
     * 月最大流量出现时间
     */
    @TableField("TDMAQT")
    private Date tdmaqt;

    /**
     * 月最小流量出现时间
     */
    @TableField("TDMIQT")
    private Date tdmiqt;

    /**
     * 月累计引水量（含公摊水量）
     */
    @TableField("FWQT")
    private Double fwqt;

    /**
     * 数据流水号
     */
    @TableField("GDWR_ATID")
    private String gdwrAtid;

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
     * 平均水位
     */
    @TableField("AVZ")
    private Double avz;

    /**
     * 月累计引水量（不含公摊水量的引水口实测水量）
     */
    @TableField("FWQTT")
    private Double fwqtt;

    /**
     * 用于断面考核的取退水口水量
     */
    @TableField("FWQTDMKH")
    private Double fwqtdmkh;


}
