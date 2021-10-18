package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.commons.RedisOperationTypeEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDispatchSchemeService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDispatchSchemeMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.ModelRequestDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchScheme;
import com.nari.slsd.msrv.waterdiversion.utils.UniqueCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * @title
 * @description 调度方案服务类
 * @author bigb
 * @updateTime 2021/8/21 11:13
 * @throws
 */
@Slf4j
@Service
public class WrDispatchSchemeServiceImpl extends ServiceImpl<WrDispatchSchemeMapper, WrDispatchScheme> implements IWrDispatchSchemeService {

    @Autowired
    private WrDispatchSchemeMapper wrDispatchSchemeMapper;

    @Autowired
    private UniqueCodeGenerateUtil uniqueCodeGenerateUtil;

    @Override
    public String generateWrDispatchScheme(ModelRequestDto modelRequestDto) {
        WrDispatchScheme scheme = new WrDispatchScheme();
        scheme.setId(IDGenerator.getId());
        //方案编号
        String key = modelRequestDto.getDispatchType() + DateUtil.today();
        String uniqueCode = uniqueCodeGenerateUtil.generateUniqueCode(key, modelRequestDto.getDispatchType(), true, 6);
        scheme.setSchemeCode(uniqueCode);
        String schemeName = uniqueCode;
        if(RedisOperationTypeEnum.MANUAL.equals(modelRequestDto.getDispatchType())){
            schemeName = "人工调度-" + schemeName;
        }else if(RedisOperationTypeEnum.PLAN.equals(modelRequestDto.getDispatchType())){
            schemeName = "计划调度-" + schemeName;
        }
        //方案名称
        scheme.setSchemeName(schemeName);
        //操作人
        scheme.setPersonId(modelRequestDto.getPersonId());
        //执行开始时间
        scheme.setExecuteStartTime(new Date(modelRequestDto.getResizeDate()));
        wrDispatchSchemeMapper.insert(scheme);
        return scheme.getId();
    }
}
