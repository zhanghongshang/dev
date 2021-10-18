package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrChargeDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCharge;

/**
 * @title
 * @description 水费收费服务类
 * @author bigb
 * @updateTime 2021/8/21 11:12
 * @throws
 */
public interface IWrChargeService extends IService<WrCharge> {

    DataTableVO getAllWrCharge(Integer pageIndex, Integer pageSize, WrChargeDto dto);
}
