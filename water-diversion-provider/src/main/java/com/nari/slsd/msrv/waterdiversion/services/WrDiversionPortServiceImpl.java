package com.nari.slsd.msrv.waterdiversion.services;

import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDiversionPort;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDiversionPortMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDiversionPortService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 测站-引水口管理 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Service
public class WrDiversionPortServiceImpl extends ServiceImpl<WrDiversionPortMapper, WrDiversionPort> implements IWrDiversionPortService {

}
