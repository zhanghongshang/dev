package com.nari.slsd.msrv.waterdiversion.model.fourth.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

/**
 * <p>
 * 测站采集最新水情表
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Data
@TableName(value = "WM_PROJECT_LAST_R",schema = "USER_WM")
public class WmProjectLastR implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 测站编码
     * 主键
     */
    @MppMultiId
    @TableField(value = "STCD")
    private String stcd;

    /**
     * 时间
     * 主键
     */
    @MppMultiId
    @TableField("TM")
    private Date tm;

    /**
     * 扩展码
     */
    @TableField("EXKEY")
    private String exkey;

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
     * 断面过水面积
     */
    @TableField("XSA")
    private Double xsa;

    /**
     * 断面平均流速
     */
    @TableField("XSAVV")
    private Double xsavv;

    /**
     * 断面最大流速
     */
    @TableField("XSMXV")
    private Double xsmxv;

    /**
     * 河水特征码
     */
    @TableField("FLWCHRCD")
    private String flwchrcd;

    /**
     * 水势
     */
    @TableField("WPTN")
    private String wptn;

    /**
     * 测流方法
     */
    @TableField("MSQMT")
    private String msqmt;

    /**
     * 测积方法
     */
    @TableField("MSAMT")
    private String msamt;

    /**
     * 测速方法
     */
    @TableField("MSVMT")
    private String msvmt;

    /**
     * 雨量
     */
    @TableField("YP")
    private Double yp;

    /**
     * 设备温度
     */
    @TableField("TEMP")
    private Double temp;

    /**
     * 信号强度
     */
    @TableField("SI")
    private Double si;

    /**
     * 设备电压
     */
    @TableField("BAT_V")
    private Double batV;

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


}
