package com.nari.slsd.msrv.waterdiversion.services;

import com.nari.slsd.msrv.waterdiversion.model.secondary.po.WrCurveOriginal;
import com.nari.slsd.msrv.waterdiversion.mapper.secondary.WrCurveOriginalMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCurveOriginalService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 原始率定数据表 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-16
 */
@Service
public class WrCurveOriginalServiceImpl extends ServiceImpl<WrCurveOriginalMapper, WrCurveOriginal> implements IWrCurveOriginalService {

}
