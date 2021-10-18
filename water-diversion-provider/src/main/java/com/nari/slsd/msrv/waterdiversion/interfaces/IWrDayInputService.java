package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.config.excel.WrDayInputInDayModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrDayInputDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInput;
import com.nari.slsd.msrv.waterdiversion.model.vo.MngUnitAndBuilding;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDayInputTable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 日水情录入 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-19
 */

public interface IWrDayInputService extends IService<WrDayInput> {

    /**
     * 批量更新或保存日水情数据
     *
     * @param dtoList
     */
    void saveOrUpdateBatch(List<WrDayInputDTO> dtoList);

    /**
     * 批量审核日水情数据
     *
     * @param dtoList
     */
    void verifyBatch(List<WrDayInputDTO> dtoList);


    /**
     * 按条件查询日水情数据,按管理单位-测站格式封装
     * 并对各状态数据进行计数
     *
     * @param mngUnitIds    管理单位ID
     * @param buildingTypes 测站类型
     * @param fillReport    是否填报 1是 0否
     * @param buildingLevels        测站层级
     * @param time          日期
     * @param status        状态 2已审核 1已录入未审核 0未录入
     */
    WrDayInputTable getDayInputDataTable(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels, Long time, Integer status);

    @Transactional(rollbackFor = {Exception.class})
    void importInDayForDayInput(String operator, String year, String month, List<WrDayInputInDayModel> inputList);
}
