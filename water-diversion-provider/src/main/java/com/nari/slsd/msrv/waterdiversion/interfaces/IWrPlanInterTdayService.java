package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrPlanInterTdayDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrPlanInterTday;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 旬迭代
 */
@Repository
public interface IWrPlanInterTdayService extends IService<WrPlanInterTday> {

    void updateTday(List<WrPlanInterTdayDTO> wrPlanInterTDayDTOList);

    WrPlanInterTday wrPlanInterTday(Date time, String buildingId,String timeType);
}
