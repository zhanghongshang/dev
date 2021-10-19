package com.nari.slsd.msrv.waterdiversion.param;
/**
 * 插值方法枚举
 * @author Chenyi
 *
 */
public enum InsertMethod {
	M_NULL,//无效插值方法
	M_2_LINE,//二项线性插值
	M_2_ESPL,//二项三次样条插值
	M_3_LINE//三项四角插值
}
