package com.nari.slsd.msrv.waterdiversion.interfaces;

import com.nari.slsd.msrv.waterdiversion.model.dto.DataBuildingDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataSetBuildingDto;

import java.util.List;

/**
 * @description: 数据接口
 * @author: Created by ZHD
 * @date: 2021/8/16 17:36
 * @return:
 */
public interface IDataService {
    /**
     * 字段说明：
     * valType :Param.ValType.*****
     * runDataType:Param.RunDataType.*****
     * calcType:Param.CalcType.*****
     */
    // 读取实时数据表 :根据测站ID以及测点类型获取  测值接口
    List<DataBuildingDto> getSpecialDataRunRtreal(List<String> buildings, List<String> pointType);
    //
    //读取其他类型表 :根据测站id、测点类型、开始结束时间、值类型、表类型 获取测值接口
    List<DataBuildingDto> getSpecialDataRunDataType(List<String> buildings,List<String> pointType,
                                                    Long sdt,Long edt,
                                                    String valType,String runDataType,String calcType );
    //根据测站id、测点类型、开始结束时间、值类型、表类型 获取过程测值接口
    List<DataSetBuildingDto> getSpecialDataSet(List<String> buildings, List<String> pointType,
                                               Long sdt, Long edt,
                                               String valType, String runDataType);
}
