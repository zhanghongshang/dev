package com.nari.slsd.msrv.waterdiversion.model.third.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author bigb
 * @title
 * @description 巴州/第二师月滚存指标
 * @updateTime 2021/9/10 19:55
 * @throws
 */
@Data
@TableName(value = "WR_JCMD_O", schema = "USER_WR")
public class WrJcmdO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理局编码年月日序号
     */
    @TableField(value = "DMID", select = false)
    private String dmId;

    /**
     * 是否首次编制
     */
    @TableField("ISFDM")
    private String isFdm;

    /**
     * 是否追加调令
     */
    @TableField(value = "ISADM", select = false)
    private String isAdm;

    /**
     * 1:1季度调令模式,2:2季度调令模式,3:3季度调令模式,4:4季度调令模式,5:月调令模式
     */
    @TableField("DTYPE")
    private String dType;

    /**
     * 流域代码
     */
    @TableField(value = "BASCD", select = false)
    private String bascd;

    /**
     * 年份
     */
    @TableField("YR")
    private Long year;

    /**
     * 月份
     */
    @TableField("MNTH")
    private Long month;

    /**
     * 来水频率
     */
    @TableField(value = "MWF", select = false)
    private Double mwf;

    /**
     * 用水单位编码
     */
    @TableField(value = "DFAGCD")
    private String dfagcd;

    /**
     * 年修正计划总用水量
     */
    @TableField("YMWM")
    private Double ymwm;

    /**
     * 1月计划用水量
     */
    @TableField("JWM")
    private Double month1;

    /**
     * 2月计划用水量
     */
    @TableField("FWM")
    private Double month2;

    /**
     * 3月计划用水量
     */
    @TableField("MWM")
    private Double month3;

    /**
     * 4月计划用水量
     */
    @TableField("AWM")
    private Double month4;

    /**
     * 5月计划用水量
     */
    @TableField("MAWM")
    private Double month5;

    /**
     * 6月计划用水量
     */
    @TableField("JUWM")
    private Double month6;

    /**
     * 7月计划用水量
     */
    @TableField("JLWM")
    private Double month7;

    /**
     * 8月计划用水量
     */
    @TableField("AUWM")
    private Double month8;

    /**
     * 9月计划用水量
     */
    @TableField("SWM")
    private Double month9;

    /**
     * 10月计划用水量
     */
    @TableField("OWM")
    private Double month10;

    /**
     * 11月计划用水量
     */
    @TableField("NWM")
    private Double month11;

    /**
     * 12月计划用水量
     */
    @TableField("DWM")
    private Double month12;

    /**
     * 年总配水量
     */
    @TableField("YOWM")
    private Double yowm;

    /**
     * 局级审核人
     */
    @TableField(value = "DMDE", select = false)
    private String dmde;

    /**
     * 0：审核中；1：审核后并下达
     */
    @TableField("DSTATE")
    private String state;

    /**
     * 处级审核人
     */
    @TableField(value = "DMMAN", select = false)
    private String dmMan;

    /**
     * 备注
     */
    @TableField(value = "NT", select = false)
    private String nt;

    /**
     * 机构代码
     */
    @TableField(value = "GDWR_OGID", select = false)
    private String gdwrOgid;

    /**
     * 交换标示位
     */
    @TableField(value = "GDWR_SDFL", select = false)
    private String gdwrSdfl;

    /**
     * 编制人
     */
    @TableField(value = "GDWR_MDPS", select = false)
    private String gdwrMdps;

    /**
     * 审核日期
     */
    @TableField(value = "GDWR_MDDT", select = false)
    private Date gdwrMdDate;

    /**
     * 数据来源
     */
    @TableField(value = "GDWR_DASC", select = false)
    private String gdwrDasc;

    /**
     * 数据说明
     */
    @TableField(value = "GDWR_DSPT", select = false)
    private String gdwrDspt;

    /**
     * 审核状态:
     * 10：方案已编制但未提交审核,11：确认编制完成并提交给副处级 ,12：确认编制完成并提交给处级,
     * 20：副处级提交意见,21：副处级完成审核并提交给处级,22：副处级完成审核并提交给副局级,
     * 30：处级提交意见,31：处级完成审核并提交给副局级,32：处级完成审核并提交给局级,
     * 40：副局级提交意见,41：副局级完成审核并提交给局级,42：副局级确认方案并下发（同时DSTATE字段变为1，即方案完成审核并下达）,
     * 50：局级提交意见,51：局级确认方案并下发（同时DSTATE字段变为1，即方案完成审核并下达）
     */
    @TableField("GDWR_DAFL")
    private String gdwrDafl;

    /**
     * 数据流水号
     */
    @TableField(value = "GDWR_ATID", select = false)
    private String gdwrAtId;

    /**
     * 编制说明
     */
    @TableField(value = "INS", select = false)
    private String ins;

    /**
     * 处级审核说明
     */
    @TableField(value = "INSCHU", select = false)
    private String insChu;

    /**
     * 局级审核说明
     */
    @TableField(value = "INSJU", select = false)
    private String insju;

    /**
     * 副局级审核人
     */
    @TableField(value = "ADMDE", select = false)
    private String admde;

    /**
     * 副处级审核人
     */
    @TableField(value = "ADMMAN", select = false)
    private String admMan;

    /**
     * 副局级审核说明
     */
    @TableField(value = "AINSJU", select = false)
    private String ainsJu;

    /**
     * 副处级审核说明
     */
    @TableField(value = "AINSCHU", select = false)
    private String ainsChu;

    /**
     * 副处级审核时间
     */
    @TableField(value = "ACHUETM", select = false)
    private Date achueTime;

    /**
     * 处级审核时间
     */
    @TableField(value = "CHUETM", select = false)
    private Date chueTime;

    /**
     * 副局级审核时间
     */
    @TableField(value = "AJUETM", select = false)
    private Date ajueTime;

    /**
     * 局级审核时间
     */
    @TableField(value = "JUETM", select = false)
    private Date jueTime;

}
