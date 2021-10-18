package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Created by ZHD
 * @program: HisDataDto
 * @description:
 * @date: 2021/8/31 14:17
 */
@Data
public class HisDataDto {
    private String building;//测站id
    private List<String> pointType;//测点类型
    private String runDataType;//读取的表名
    private String valType;//获取的值类型
    private List<Long>[] date;//时间戳区间，第一个为开始时间，第二个为结束时间，此参数为获取日数据必传
    private List<Integer> years;//需要读取的年限
}
