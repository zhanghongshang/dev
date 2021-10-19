package com.nari.slsd.msrv.waterdiversion.param;
/**
 * 插值类型枚举
 * @author Chenyi
 *
 */
public enum InsertType {
	D_NULL,//无效插值方向
	D_1_2,//由0列数字插1列数值
	D_2_1,//由1列数字插0列数值
	D_12_3,//由0,1列数字插2列数值
	D_13_2,//由0,2列数字插1列数值
	D_23_1//由1,2列数字插0列数值
}
