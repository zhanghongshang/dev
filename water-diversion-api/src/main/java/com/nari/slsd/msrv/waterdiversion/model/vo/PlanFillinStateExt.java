package com.nari.slsd.msrv.waterdiversion.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Program: water-diversion
 * @Description: 流程状态流程id 填报数据整合vo类
 * @Author: zhs
 * @Date: 2021-08-26
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanFillinStateExt {
    //流程状态
    String state;
    //流程id
    String processId;
    //任务id
    String palnId;
    //填报数据
    List<BuildingExt> buildingExtList;
}
