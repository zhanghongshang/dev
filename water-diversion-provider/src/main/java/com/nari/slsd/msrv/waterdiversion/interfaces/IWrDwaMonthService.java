package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDwaMonth;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDwaMonthVO;

import java.util.List;

/**
 * @title
 * @description 月滚存指标服务类
 * @author bigb
 * @updateTime 2021/9/20 20:12
 * @throws
 */
public interface IWrDwaMonthService extends IService<WrDwaMonth> {

    DataTableVO findWdaValue(List<String> mngUnitId, String buildName, String time, List<Integer> buildingLevels,
                             Integer pageIndex, Integer pageSize);
}
