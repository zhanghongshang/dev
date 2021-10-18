package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrFeeRateDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrFeeRate;

import java.util.List;

/**
 * @title
 * @description 水费费率服务类
 * @author bigb
 * @updateTime 2021/8/21 11:12
 * @throws
 */
public interface IWrFeeRateService extends IService<WrFeeRate> {

    int addWrFeeRate(WrFeeRateDto wrFeeRateDto);

    boolean batchUpdateWrFee(String categoryId, List<WrFeeRateDto> wrFeeRateDtoList);
}
