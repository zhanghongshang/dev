package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.model.dto.ModelRequestDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchScheme;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchSchemeResult;

/**
 * @title
 * @description 调度方案服务类
 * @author bigb
 * @updateTime 2021/8/21 11:12
 * @throws
 */
public interface IWrDispatchSchemeService extends IService<WrDispatchScheme> {
    /**
     * 生成调度方案信息
     * @param modelRequestDto
     * @return
     */
    String generateWrDispatchScheme(ModelRequestDto modelRequestDto);
}
