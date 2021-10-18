package com.nari.slsd.msrv.waterdiversion.services;

import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurvePointValue;
import com.nari.slsd.msrv.waterdiversion.mapper.secondary.WrCurvePointValueMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCurvePointValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 曲线点值定义 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-08
 */
@Service
public class WrCurvePointValueServiceImpl extends ServiceImpl<WrCurvePointValueMapper, WrCurvePointValue> implements IWrCurvePointValueService {

}
