package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.waterdiversion.config.excel.WrDayInputInMonthModel;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrDayInmonthInput;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrDayInmonthInputTable;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrFlowDayInmonthRow;

import java.util.List;


/**
 * <p>
 * 逐日水情录入 服务类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-19
 */

public interface IWrDayInMonthInputService extends IService<WrDayInmonthInput> {

    /**
     * 逐日水情数据校核
     *
     * @param dto
     */
    void updateBatch(WrFlowDayInmonthRow dto);

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
    WrDayInmonthInputTable getDayInmonthInputTable(List<String> mngUnitIds, List<String> buildingTypes, Integer fillReport, List<Integer> buildingLevels, Long time, Integer status);


    void importInMonthForDayInput(String operator , String year , List<WrDayInputInMonthModel> inputList);
}
