package com.nari.slsd.msrv.waterdiversion.services;

import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.nari.slsd.msrv.waterdiversion.model.third.po.WrDinwS;
import com.nari.slsd.msrv.waterdiversion.mapper.third.WrDinwSMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDinwSService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 地表水取水口日引水监测表（月校核日监测数据表） 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-30
 */
@Service
public class WrDinwSServiceImpl extends MppServiceImpl<WrDinwSMapper, WrDinwS> implements IWrDinwSService {

}
