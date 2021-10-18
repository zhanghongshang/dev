package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Program: water-diversion
 * @Description: 测站采集最新水情表VO
 * @Author: reset kalar
 * @Date: 2021-08-31 17:15
 **/
@Data
@NoArgsConstructor
public class WmProjectLastRVO {
    /**
     * 测站编码
     */
    private String stcd;

    /**
     * 测站名称
     */
    private String stnm;

    /**
     * 时间
     */
    private Long tm;

//    /**
//     * 扩展码
//     */
//    private String exkey;

    /**
     * 水位
     */
    private Double z;

    /**
     * 流量
     */
    private Double q;

//    /**
//     * 断面过水面积
//     */
//    private Double xsa;
//
//    /**
//     * 断面平均流速
//     */
//    private Double xsavv;
//
//    /**
//     * 断面最大流速
//     */
//    private Double xsmxv;

//    /**
//     * 河水特征码
//     */
//    private String flwchrcd;

//    /**
//     * 水势
//     */
//    private String wptn;

//    /**
//     * 测流方法
//     */
//    private String msqmt;

//    /**
//     * 测积方法
//     */
//    private String msamt;

//    /**
//     * 测速方法
//     */
//    private String msvmt;

//    /**
//     * 雨量
//     */
//    private Double yp;

//    /**
//     * 设备温度
//     */
//    private Double temp;

//    /**
//     * 信号强度
//     */
//    private Double si;

//    /**
//     * 设备电压
//     */
//    private Double batV;

//    /**
//     * 机构代码
//     */
//    private String gdwrOgid;

//    /**
//     * 交换标示位
//     */
//    private String gdwrSdfl;

//    /**
//     * 修改人
//     */
//    private String gdwrMdps;

//    /**
//     * 修改日期
//     */
//    private Long gdwrMddt;
//
//    /**
//     * 数据来源
//     */
//    private String gdwrDasc;
//
//    /**
//     * 数据说明
//     */
//    private String gdwrDspt;
//
//    /**
//     * 数据标识
//     */
//    private String gdwrDafl;
//
//    /**
//     * 数据流水号
//     */
//    private String gdwrAtid;

}
