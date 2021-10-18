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
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.InstructionEnum;
import com.nari.slsd.msrv.waterdiversion.commons.RedisOperationTypeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrCmdManagerEnum;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrCmdManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDispatchInstructionService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrCmdManagerMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDispatchInstructionMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDiversionPortMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCmdManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrCmdManagerOperateDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrDispatchInstructionDto;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrCmdManager;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDispatchInstruction;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDiversionPort;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrCmdManagerVO;
import com.nari.slsd.msrv.waterdiversion.utils.UniqueCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nari.slsd.msrv.common.utils.DateUtils.convertTimeToString;
import static com.nari.slsd.msrv.waterdiversion.commons.InstructionEnum.*;
import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.CONVERTER_DATETIME_TO_LONG;
import static com.nari.slsd.msrv.waterdiversion.processer.ProcessorFactory.getConverterInstance;
import static com.nari.slsd.msrv.waterdiversion.utils.CommonUtil.convert2EntityList;


/**
 * @title
 * @description 调度指令管理服务类
 * @author bigb
 * @updateTime 2021/8/21 11:13
 * @throws
 */
@Slf4j
@Service
public class WrCmdManagerServiceImpl extends ServiceImpl<WrCmdManagerMapper, WrCmdManager> implements IWrCmdManagerService {
    private static final String ORDER_TEMPLATE = "调度指令【{0}】号";

    @Autowired
    private IWrDispatchInstructionService wrDispatchInstructionService;

    @Resource
    private IModelCacheService modelCacheService;

    @Autowired
    private WrCmdManagerMapper wrCmdManagerMapper;

    @Autowired
    private WrDispatchInstructionMapper wrDispatchInstructionMapper;

    @Autowired
    private UniqueCodeGenerateUtil uniqueCodeGenerateUtil;

    /**
     * 分页查询调度指令管理信息
     * @param pageModel
     * @return
     */
    @Override
    public DataTableVO getWrCmdManagerPage(PageModel pageModel) {
        WrCmdManagerOperateDto dto = (WrCmdManagerOperateDto) pageModel.getSearchData();
        //分页查询
        LambdaQueryWrapper<WrCmdManager> pageWrapper = new QueryWrapper<WrCmdManager>().lambda();
        //根据调令名称模糊查询
        if(StringUtils.isNotEmpty(dto.getOrderName())){
            pageWrapper.like(WrCmdManager::getOrderName,dto.getOrderName());
        }
        //根据执行单位筛选
        if(CollectionUtils.isNotEmpty(dto.getManageUnitIdList())){
            pageWrapper.in(WrCmdManager::getManageUnitId,dto.getManageUnitIdList());
        }
        //根据调令状态筛选
        if(StringUtils.isNotEmpty(dto.getOrderStatus())){
            pageWrapper.eq(WrCmdManager::getOrderStatus,dto.getOrderStatus());
        }
        //根据年份筛选
        if(StringUtils.isNotEmpty(dto.getYear())){
            pageWrapper.eq(WrCmdManager::getYear,dto.getYear());
        }
        //根据下达时间筛选
        if(null != dto.getOrderStartTime() && null != dto.getOrderEndTime()){
            pageWrapper.between(WrCmdManager::getOrderTime,
                    DateUtils.convertTimeToDate(dto.getOrderStartTime()) ,
                        DateUtils.convertTimeToDate(dto.getOrderEndTime()));
        }
        //根据调令编制人
        if(StringUtils.isNotEmpty(dto.getLaunchName())){
            pageWrapper.like(WrCmdManager::getLaunchName,dto.getLaunchName());
        }
        //个人待办只能看到已审批的数据
        if(dto.isQueryMine()){
            pageWrapper.eq(WrCmdManager::getOrderStatus,WrCmdManagerEnum.APPROVED);
        }
        pageWrapper.orderByDesc(WrCmdManager::getCreateTime);
        IPage<WrCmdManager> selectPage = wrCmdManagerMapper.selectPage(new Page<>(pageModel.getStart(), pageModel.getPageSize()),pageWrapper);
        List<WrCmdManager> resultList = selectPage.getRecords();
        if(CollectionUtils.isNotEmpty(resultList)){
            Map<String,Object> setMap = new HashMap<String, Object>(4){
                {
                    put("CREATE_TIME",getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                    put("ORDER_TIME",getConverterInstance(CONVERTER_DATETIME_TO_LONG));
                }
            };
            List<WrCmdManagerVO> voList = convert2EntityList(resultList, WrCmdManagerVO.class, setMap);
            voList.forEach(vo -> {
                //存的是用户名id
                String launchId = vo.getLaunchName();
                vo.setLaunchName(modelCacheService.getRealName(launchId));
            });
            DataTableVO dataTableVO = new DataTableVO();
            dataTableVO.setRecordsFiltered(selectPage.getTotal());
            dataTableVO.setRecordsTotal(selectPage.getTotal());
            dataTableVO.setData(voList);
            return dataTableVO;
        }
        return null;
    }

    /**
     * @title insertWrCmdManager
     * @description 调度指令管理新增
     * @author bigb
     * @param: dto
     * @updateTime 2021/8/29 12:12
     * @return: int
     * @throws
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWrCmdManager(WrCmdManagerOperateDto dto){
        /**
         * 指令管理表新增
         */
        WrCmdManager wrCmdManager = new WrCmdManager();
        BeanUtils.copyProperties(dto,wrCmdManager);
        wrCmdManager.setId(IDGenerator.getId());
        //调令编码
        String key = RedisOperationTypeEnum.WATER_CMD_MANAGER + DateUtil.today();
        String uniqueCode = uniqueCodeGenerateUtil.generateUniqueCode(key, RedisOperationTypeEnum.WATER_CMD_MANAGER, true, 4);
        wrCmdManager.setOrderCode(uniqueCode);
        //调令名称
        String orderName = StringUtils.replaceEach(ORDER_TEMPLATE, new String[]{"{0}"}, new String[]{uniqueCode});
        wrCmdManager.setOrderName(orderName);
        //指令状态
        wrCmdManager.setOrderStatus(WrCmdManagerEnum.PENDING_APPROVE);
        //年份
        wrCmdManager.setYear(String.valueOf(DateUtil.thisYear()));
        wrCmdManagerMapper.insert(wrCmdManager);
        /**
         * 调度指令新增
         */
        if(CollectionUtils.isNotEmpty(dto.getInstructionDtoList())){
            List<WrDispatchInstructionDto> instructionDtoList = dto.getInstructionDtoList();
            List<WrDispatchInstruction> instructionList = new ArrayList<>();
            instructionDtoList.stream().forEach(instructionDto -> {
                WrDispatchInstruction instruction = new WrDispatchInstruction();
                instruction.setId(IDGenerator.getId());
                //引水口id
                instruction.setBuildingId(instructionDto.getBuildingId());
                //调整时间
                instruction.setStartTime(DateUtils.convertTimeToDate(instructionDto.getResizeTime()));
                //目标值
                instruction.setSetValue(instructionDto.getSetValue());
                //新建时，目标调整值和目标值设置相同值
                instruction.setModifyValue(instructionDto.getSetValue());
                //待执行
                instruction.setStatus(InstructionEnum.PENDING_DISPATCH_ISSUE);
                instructionList.add(instruction);
            });
            wrDispatchInstructionService.saveBatch(instructionList);
        }
        return 1;
    }

    /**
     * 管理站审批或下达指令
     * @param managerId
     * @param approveStatus
     * @param approveName
     * @param approveContent
     * @return
     */
    @Override
    public int updateWrCmdManager(String managerId , String approveStatus , String approveName , String approveContent){
        if(StringUtils.isEmpty(managerId)){
            throw new TransactionException(CodeEnum.NO_PARAM,"请传入待修改的指令管理信息！");
        }
        WrCmdManager wrCmdManager = wrCmdManagerMapper.selectById(managerId);
        if(null == wrCmdManager){
            throw new TransactionException(CodeEnum.NO_DATA,"未查询到指令管理信息，id is " + managerId);
        }

        //审批时间即为下达时间
        wrCmdManager.setOrderTime(DateUtil.date());
        //审批人
        wrCmdManager.setApproveName(approveName);
        //审批通过
        wrCmdManager.setOrderStatus(approveStatus);
        //审批意见
        wrCmdManager.setApproveContent(approveContent);
        return wrCmdManagerMapper.updateById(wrCmdManager);
    }

    /**
     * 删除调度管理及关联指令信息
     * @param cmdManagerId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWrCmdManager(String cmdManagerId){
        if(StringUtils.isEmpty(cmdManagerId)){
            throw new TransactionException(CodeEnum.NO_PARAM,"请传入待删除的调度指令管理信息！");
        }
        //指令管理删除
        wrCmdManagerMapper.deleteById(cmdManagerId);
        //关联指令集删除
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("ORDER_ID",cmdManagerId);
        wrDispatchInstructionMapper.deleteByMap(paramMap);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWrCmdManager(WrCmdManagerDTO dto) {
        //生成指令管理表
        String managerId = saveWrManager(dto);
        //生成调度指令
        saveInstruction(dto, managerId);
    }

    private void saveInstruction(WrCmdManagerDTO dto, String managerId) {
        /**Date now = DateUtil.date();
        LambdaQueryWrapper<WrDispatchInstruction> queryWrapper = new QueryWrapper().lambda();
        queryWrapper.eq(WrDispatchInstruction::getActiveFlag,1);
        queryWrapper.eq(WrDispatchInstruction::getBuildingId,buildingId);
        queryWrapper.gt(WrDispatchInstruction::getEndTime,now);
        //使用的是逻辑删除
        wrDispatchInstructionMapper.delete(queryWrapper);*/
        //生成执行指令
        WrDispatchInstruction instruction = new WrDispatchInstruction();
        instruction.setId(IDGenerator.getId());
        instruction.setCmdManagerId(managerId);
        instruction.setBuildingId(dto.getBuildingId());
        instruction.setBuildingName(dto.getBuildingName());
        instruction.setStartTime(DateUtils.convertTimeToDate(dto.getExecuteStartTime()));
        instruction.setEndTime(DateUtils.convertTimeToDate(dto.getExecuteEndTime()));
        //原值
        instruction.setSourceValue(dto.getWaterFlowBefore());
        //目标值
        instruction.setSetValue(dto.getWaterFlowAfter());
        //根据引水口是否远程可控,设置指令初始状态
        instruction.setStatus(PENDING_DISPATCH_ISSUE);
        instruction.setPersonId(dto.getLaunchName());
        instruction.setCommandType(SCHEDULE_MANUAL);
        //生成调度指令信息
        wrDispatchInstructionMapper.insert(instruction);
    }

    private String saveWrManager(WrCmdManagerDTO dto) {
        WrCmdManager manager = new WrCmdManager();
        manager.setId(IDGenerator.getId());
        //调令编码
        String key = RedisOperationTypeEnum.WATER_CMD_MANAGER_REAL + DateUtil.today();
        String uniqueCode = uniqueCodeGenerateUtil.generateUniqueCode(key, RedisOperationTypeEnum.WATER_CMD_MANAGER, true, 4);
        manager.setOrderCode(uniqueCode);
        //调令名称
        String orderName = StringUtils.replaceEach(ORDER_TEMPLATE, new String[]{"{0}"}, new String[]{uniqueCode});
        manager.setOrderName(orderName);
        //实时调度指令,待审批
        manager.setOrderStatus(WrCmdManagerEnum.PENDING_APPROVE);
        //指令类型:手工生成指令
        manager.setOrderType(WrCmdManagerEnum.MANUAL_ORDER);
        //年份
        manager.setYear(String.valueOf(DateUtil.thisYear()));
        //管理站名称
        String mngUnitName = modelCacheService.getMngUnitName(dto.getManageUnitId());
        //指令内容 TODO 流量空指针判断没做
        String orderContent = StringUtils.replaceEach(ORDER_CONTENT_TEMPLATE,
                new String[]{"{0}","{1}","{2}","{3}","{4}","{5}"},
                new String[]{StringUtils.defaultString(mngUnitName,""),
                        convertTimeToString(dto.getExecuteStartTime()),
                        convertTimeToString(dto.getExecuteEndTime()),
                        dto.getBuildingName(),
                        StringUtils.defaultString(String.valueOf(dto.getWaterFlowBefore()),""),
                        StringUtils.defaultString(String.valueOf(dto.getWaterFlowAfter()),"")});
        manager.setOrderContent(orderContent);
        //指令编制人
        manager.setLaunchName(dto.getLaunchName());
        //管理站id
        manager.setManageUnitId(dto.getManageUnitId());
        //审批人
        manager.setApproveName("SYSTEM");
        //审批意见
        manager.setApproveContent("流程已审批,系统自动审批");
        manager.setActiveFlag(1);
        wrCmdManagerMapper.insert(manager);
        return manager.getId();
    }
}
