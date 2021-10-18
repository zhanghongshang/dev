package com.nari.slsd.msrv.waterdiversion.services;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.commons.InstructionEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCmdRecordService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDispatchInstructionService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDispatchInstructionMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDiversionPortMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrDispatchInstructionDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCmdRecord;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchInstruction;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDiversionPort;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDispatchInstructionVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.CONVERTER_DATETIME_TO_LONG;
import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.getConverterInstance;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.convert2EntityList;


/**
 * @author bigb
 * @title
 * @description 调度方案指令服务类
 * @updateTime 2021/8/21 11:13
 * @throws
 */
@Slf4j
@Service
public class WrDispatchInstructionServiceImpl extends ServiceImpl<WrDispatchInstructionMapper, WrDispatchInstruction> implements IWrDispatchInstructionService {
    @Autowired
    private WrDispatchInstructionMapper wrDispatchInstructionMapper;

    @Autowired
    private WrDiversionPortMapper wrDiversionPortMapper;

    @Autowired
    private IWrCmdRecordService wrCmdRecordService;

    /**
     * 获取所有待执行调度指令
     *
     * @return
     */
    @Override
    public List<WrDispatchInstructionVO> getAllPendingIssueOrder() {
        LambdaQueryWrapper<WrDispatchInstruction> wrapper = new QueryWrapper().lambda();
        wrapper.eq(WrDispatchInstruction::getStatus, InstructionEnum.PENDING_DISPATCH_ISSUE);
        List<WrDispatchInstruction> instructionList = wrDispatchInstructionMapper.selectList(wrapper);
        Map<String, Object> setMap = new HashMap<String, Object>(4) {
            {
                put("startTime", getConverterInstance(CONVERTER_DATETIME_TO_LONG));
            }
        };
        return convert2EntityList(instructionList, WrDispatchInstructionVO.class, setMap);
    }

    /**
     * 分页查询调度指令管理信息
     *
     * @param pageModel
     * @return
     */
    @Override
    public DataTableVO getWrDispatchInstructionPage(PageModel pageModel, String mangerId) {
        LambdaQueryWrapper<WrDispatchInstruction> wrapper = new QueryWrapper().lambda();
        wrapper.eq(WrDispatchInstruction::getCmdManagerId, mangerId);
        IPage<WrDispatchInstruction> selectPage = wrDispatchInstructionMapper.selectPage(new Page<>(pageModel.getStart(), pageModel.getPageSize()), wrapper);
        List<WrDispatchInstruction> resultList = selectPage.getRecords();
        if (CollectionUtils.isNotEmpty(resultList)) {
            Map<String, Object> setMap = new HashMap<String, Object>(4) {
                {
                    put("startTime", getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                    put("sendTime", getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                    put("finishTime", getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                }
            };
            List<WrDispatchInstructionVO> voList = convert2EntityList(resultList, WrDispatchInstructionVO.class, setMap);
            DataTableVO dataTableVO = new DataTableVO();
            dataTableVO.setRecordsFiltered(selectPage.getTotal());
            dataTableVO.setRecordsTotal(selectPage.getTotal());
            dataTableVO.setData(voList);
            return dataTableVO;
        }
        return null;
    }

    /**
     * @throws
     * @title batchUpdateInstruction
     * @description 指令批量修改
     * @author bigb
     * @updateTime 2021/8/21 16:29
     */
    @Override
    public void batchUpdateInstruction(List<WrDispatchInstructionDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            throw new TransactionException(CodeEnum.NO_PARAM, "请传入批量修改的指令列表！");
        }
        Map<String, WrDispatchInstructionDto> dtoMap = dtoList.stream().collect(Collectors.toMap(WrDispatchInstructionDto::getInstructionId, dto -> dto));
        QueryWrapper<WrDispatchInstruction> instructionQueryWrapper = new QueryWrapper<>();
        instructionQueryWrapper.in("ID", dtoMap.keySet());
        List<WrDispatchInstruction> instructionList = wrDispatchInstructionMapper.selectList(instructionQueryWrapper);
        if (CollectionUtils.isEmpty(instructionList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何指令信息,ID列表为: " + dtoMap.keySet().toString());
        }
        instructionList.stream().filter(instruction -> dtoMap.get(instruction.getId()) != null).forEach(instruction -> {
            WrDispatchInstructionDto dto = dtoMap.get(instruction.getId());
            if (null != dto.getModifyValue()) {
                instruction.setModifyValue(dto.getModifyValue());
            }
            if (null != dto.getResizeTime()) {
                instruction.setStartTime(DateUtils.convertTimeToDate(dto.getResizeTime()));
            }
        });
        this.saveOrUpdateBatch(instructionList);
    }

    /**
     * @throws
     * @title sendInstruction
     * @description 指令执行
     * @author bigb
     * @param: idList
     * @updateTime 2021/8/21 16:29
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeInstruction(String id, String operateType, String personId, String executeContent) {
        if (StringUtils.isEmpty(id)) {
            throw new TransactionException(CodeEnum.NO_PARAM, "请传入下发指令ID列表！");
        }
        //TODO 生成控制指令
        //根据指令id列表,查询所有指令信息
        //先查询方案指令表,如果已经生成指令,则不可显示方案结果信息
        QueryWrapper<WrDispatchInstruction> instructionQueryWrapper = new QueryWrapper<>();
        instructionQueryWrapper.eq("ID", id);
        List<WrDispatchInstruction> instructionList = wrDispatchInstructionMapper.selectList(instructionQueryWrapper);
        if (CollectionUtils.isEmpty(instructionList)) {
            throw new TransactionException(CodeEnum.NO_DATA, "未查询到任何指令信息,ID列表为: " + id);
        }
        List<WrCmdRecord> recordList = new ArrayList<>();
        instructionList.stream().forEach(instruction -> {
            if (InstructionEnum.OPERATE_DISPATCH_SEND.equals(operateType)) {
                WrDiversionPort wrDiversionPort = wrDiversionPortMapper.selectById(instruction.getBuildingId());
                if (null == wrDiversionPort) {
                    throw new TransactionException("未查询到引水口信息,building id is " + instruction.getBuildingId());
                }
                if (0 == wrDiversionPort.getIfRemoteControl()) {
                    //待段长下发
                    instruction.setStatus(InstructionEnum.PENDING_SEGMENT_ISSUE);
                } else {
                    //待辅助确认
                    instruction.setStatus(InstructionEnum.PENDING_ASSIST_CONFIRM);
                }
            } else if (InstructionEnum.OPERATE_SEGMENT_SEND.equals(operateType)
                    || InstructionEnum.OPERATE_ASSIST_CONFIRM.equals(operateType)) {
                //指令待执行
                instruction.setStatus(InstructionEnum.PENDING_EXECUTE);
            } else if (InstructionEnum.OPERATE_EXECUTE.equals(operateType)) {
                //指令执行中
                instruction.setStatus(InstructionEnum.EXECUTING);
                //指令执行人
                instruction.setPersonId(personId);
                //执行人员操作指令时间
                instruction.setSendTime(DateUtil.date());
            } else if (InstructionEnum.OPERATE_EXECUTE_FINISHED.equals(operateType)) {
                //指令执行完成
                instruction.setStatus(InstructionEnum.FINISHED);
                //指令执行完成时间
                instruction.setFinishTime(DateUtil.date());
            }
            WrCmdRecord wrCmdRecord = new WrCmdRecord();
            wrCmdRecord.setId(IDGenerator.getId());
            wrCmdRecord.setExecuteContent(executeContent);
            wrCmdRecord.setOperator(personId);
            wrCmdRecord.setOperatorTime(DateUtil.date());
            wrCmdRecord.setCmdId(instruction.getId());
            recordList.add(wrCmdRecord);
        });
        this.saveOrUpdateBatch(instructionList);
        this.wrCmdRecordService.saveBatch(recordList);
    }
}
