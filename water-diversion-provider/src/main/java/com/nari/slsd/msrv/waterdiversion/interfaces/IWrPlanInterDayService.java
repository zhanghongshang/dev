package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanInterDayDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterDay;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterTday;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrPlanDataContrast;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Repository
public interface IWrPlanInterDayService extends IService<WrPlanInterDay> {

    void updateDay(List<WrPlanInterDayDTO> wrPlanInterDayDTOList);

    List<WrPlanInterDay> planDayValue(Date starTime,Date endTime,List<String> buildingId);

    List<WrPlanDataContrast> planInterDayValue();
}
