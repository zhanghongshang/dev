package com.nari.slsd.msrv.waterdiversion.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description 计划审批 dto类
 * @Author ZHS
 * @Date 2021/8/31 16:09
 */
@Data
public class PlanFillinExamineDTO {
    //计划任务id
    private String planId;
    //流程id
    private String processId;
    //时间格式（年/月）
    private String time;
    //登录人id
    private String userId;
    //用水单位层级
    private List<Integer> unitLevels;
    //测站类型
    private List<String> buildingTypes;
    //是否填报
    private Integer fillReport;
    //用户组配置的管理站
    private List<MngUnitGrade> mngUnitGrade;
    //引水口编码
    List<Integer> levels;

}
