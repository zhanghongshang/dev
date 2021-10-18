package com.nari.slsd.msrv.waterdiversion.model.primary.po;

import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPlanFillinMonthDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPlanFillinYearDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanInterDayDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanInterTdayDTO;
import lombok.Data;

import java.util.List;

/**
 * @Description 填报计划相关表数据存储类
 * @Author ZHS
 * @Date 2021/9/7 16:59
 */
@Data
public class WrPlanFillinRelevantTable {

    private WrPlanTask wrPlanTask;

    private List<WaterPlanFillinYear> waterPlanFillinYear;

    private List<WaterPlanFillinMonth> waterPlanFillinMonth;

    private List<WrPlanInterTday> wrPlanInterTday;

    private List<WrPlanInterDay> wrPlanInterDay;

    private List<WaterPlanFillinYearDTO> waterPlanFillInYearDTOList;

    private List<WaterPlanFillinMonthDTO> waterPlanFillinMonthDTOList;

    private List<WrPlanInterTdayDTO> wrPlanInterTDayDTOList;

    private List<WrPlanInterDayDTO> wrPlanInterDayDTOList;

}
