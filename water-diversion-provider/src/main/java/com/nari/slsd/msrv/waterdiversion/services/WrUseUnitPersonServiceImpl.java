package com.nari.slsd.msrv.waterdiversion.services;

import com.nari.slsd.msrv.waterdiversion.model.dto.WrUseUnitPersonDTO;
import com.nari.slsd.msrv.waterdiversion.model.po.WrUseUnitPerson;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrUseUnitPersonMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitPersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用水单位人员表 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-30
 */
@Slf4j
@Service
public class WrUseUnitPersonServiceImpl extends ServiceImpl<WrUseUnitPersonMapper, WrUseUnitPerson> implements IWrUseUnitPersonService {

    public static WrUseUnitPerson convert2DO(WrUseUnitPersonDTO dto) {
        WrUseUnitPerson po=new WrUseUnitPerson();
        BeanUtils.copyProperties(dto, po);
        return po;
    }
}
