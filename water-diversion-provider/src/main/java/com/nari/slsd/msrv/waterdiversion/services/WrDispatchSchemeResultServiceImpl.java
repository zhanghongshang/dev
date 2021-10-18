package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.commons.InstructionEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDispatchInstructionService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDispatchSchemeResultService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDispatchInstructionMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDispatchSchemeResultMapper;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchInstruction;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchSchemeResult;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDispatchSchemeResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @title
 * @description 调度方案结果服务类
 * @author bigb
 * @updateTime 2021/8/21 11:13
 * @throws
 */
@Slf4j
@Service
public class WrDispatchSchemeResultServiceImpl extends ServiceImpl<WrDispatchSchemeResultMapper, WrDispatchSchemeResult> implements IWrDispatchSchemeResultService {

    @Autowired
    private IWrDispatchInstructionService wrDispatchInstructionService;

    @Autowired
    private WrDispatchSchemeResultMapper wrDispatchSchemeResultMapper;

    @Autowired
    private WrDispatchInstructionMapper wrDispatchInstructionMapper;

    /**
     * 获取调度方案结果信息
     * @param schemeId
     * @return
     */
    @Override
    public List<WrDispatchSchemeResultVO> getAllSchemeResult(String schemeId){
        if(StringUtils.isEmpty(schemeId)){
            throw new TransactionException(CodeEnum.NO_PARAM,"请传入调度方案关联信息！");
        }
        //先查询方案指令表,如果已经生成指令,则不可显示方案结果信息
        QueryWrapper<WrDispatchInstruction> instructionQueryWrapper = new QueryWrapper<>();
        instructionQueryWrapper.eq("SCHEME_ID", schemeId);
        Integer count = wrDispatchInstructionMapper.selectCount(instructionQueryWrapper);
        if(null != count && count > 0){
            return null;
        }
        //查询方案结果表信息
        QueryWrapper<WrDispatchSchemeResult> schemeResultQueryWrapper = new QueryWrapper<>();
        schemeResultQueryWrapper.eq("SCHEME_ID", schemeId);
        List<WrDispatchSchemeResult> schemeResultList = wrDispatchSchemeResultMapper.selectList(schemeResultQueryWrapper);
        if(CollectionUtils.isNotEmpty(schemeResultList)){
            List<WrDispatchSchemeResultVO> voList = new ArrayList<>();
            schemeResultList.stream().forEach(schemeResult -> {
                WrDispatchSchemeResultVO vo = new WrDispatchSchemeResultVO();
                BeanUtils.copyProperties(schemeResult,vo);
                //设置引水口名称
                //vo.setBuildingName();
                if(null != schemeResult.getExecStartTime()){
                    vo.setExecStartTime(schemeResult.getExecStartTime().getTime());
                }
                voList.add(vo);
            });
        }
        return null;
    }

    /**
     * 方案指令结果信息批量确认后,生成指令表信息
     * @param idList
     */
    @Override
    public void batchConfirm(List<String> idList){
        if(CollectionUtils.isEmpty(idList)){
            throw new TransactionException(CodeEnum.NO_PARAM,"请传入调度方案结果ID列表！");
        }
        //查询方案结果表信息
        QueryWrapper<WrDispatchSchemeResult> schemeResultQueryWrapper = new QueryWrapper<>();
        schemeResultQueryWrapper.in("ID", idList);
        List<WrDispatchSchemeResult> schemeResultList = wrDispatchSchemeResultMapper.selectList(schemeResultQueryWrapper);
        if(CollectionUtils.isNotEmpty(schemeResultList)){
            throw new TransactionException(CodeEnum.NO_DATA,"未查询到任何调度方案结果信息,id集为:" + idList.toString());
        }
        List<WrDispatchInstruction> instructionList = new ArrayList<>();
        schemeResultList.stream().forEach(result -> {
            WrDispatchInstruction instruction = new WrDispatchInstruction();
            instructionList.add(instruction);
            //id
            instruction.setId(IDGenerator.getId());
            //方案id
            instruction.setSchemeId(result.getSchemeId());
            //引水口id
            instruction.setBuildingId(result.getBuildingId());
            //设备id
            instruction.setObjectId(result.getObjectId());
            //执行开始日期
            instruction.setStartTime(result.getExecStartTime());
            //测点号
            instruction.setSenId(result.getSenId());
            //目标值
            instruction.setSetValue(result.getSetValue());
            //调整时序
            instruction.setAdjustOrder(result.getAdjustOrder());
            //完成后等待时间
            instruction.setWaitTime(result.getWaitTime());
            //状态 0-待执行 1-执行中 2-执行完毕
            instruction.setStatus(InstructionEnum.PENDING_DISPATCH_ISSUE);
        });
        //生成指令集
        if(instructionList.size() > 0){
            wrDispatchInstructionService.saveBatch(instructionList);
        }
    }
}
