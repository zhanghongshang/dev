package com.nari.slsd.msrv.waterdiversion.model.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;

import java.util.List;
import java.util.Map;

/**
 * 年计划DTO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WrPlanFileYearValue {

    //人员id
    private String userId;

    private List<String> mngUnitId;

    //填报时间
    private String time;

    //填报数据和相关单位信息
    private List<Map<String,Object>> fillinValue;
    //填报内容
    private String content;

    private JSONArray jsonArray;
}
