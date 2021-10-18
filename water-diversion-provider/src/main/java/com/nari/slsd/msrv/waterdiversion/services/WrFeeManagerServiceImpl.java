package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrFeeManagerService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrFeeManagerMapper;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrFeeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @title
 * @description 水费管理服务类
 * @author bigb
 * @updateTime 2021/8/21 11:13
 * @throws
 */
@Slf4j
@Service
public class WrFeeManagerServiceImpl extends ServiceImpl<WrFeeManagerMapper, WrFeeManager> implements IWrFeeManagerService {
    @Autowired
    private WrFeeManagerMapper wrFeeManagerMapper;


}
