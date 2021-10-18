package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCmdRecordService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrCmdRecordMapper;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCmdRecord;
import org.springframework.stereotype.Service;

/**
 * @Description 指令执行记录 实现类
 * @Author ZHS
 * @Date 2021/10/17 15:10
 */
@Service
public class WrCmRecordServiceImpl extends ServiceImpl<WrCmdRecordMapper, WrCmdRecord> implements IWrCmdRecordService {

}
