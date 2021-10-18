package com.nari.slsd.msrv.waterdiversion.web;
//
//import com.nari.slsd.hu.mplat.imc.client.dto.Param;
//import com.nari.slsd.msrv.common.model.DataTableVO;
//import com.nari.slsd.msrv.common.model.PageModel;
//import com.nari.slsd.msrv.common.utils.DateUtils;
//import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.hu.mplat.imc.client.Param.Param;
import com.nari.slsd.msrv.waterdiversion.commons.PointTypeEnum;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ResponseResult;
import com.nari.slsd.msrv.waterdiversion.interfaces.IDataService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterPointService;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataBuildingDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.DataSetBuildingDto;
import com.nari.slsd.msrv.waterdiversion.model.dto.WaterPointDTO;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @description: 控制层、服务接口
 * @author: Created by ZHD
 * @date: 2021/4/1 15:56
 * @return:
 */
@Slf4j
@RestController
@RequestMapping("api/demo")
@ResponseResult //统一返回处理注解
public class test {


    @Autowired
    IWaterPointService iWaterPointService;


//    @GetMapping(value = "test")
//    public String test(){
//        List<String> buildings = new ArrayList<>();
//        buildings.add("SGJZ00000158");
//        buildings.add("SGJZ00000172");
//        List<String> pointTypes = new ArrayList<>();
//        pointTypes.add("1");
//        pointTypes.add("2");
//        List<WaterPointDTO> result  = iWaterPointService.getWaterPointId(buildings,pointTypes);
//        log.info(result.toString());
//
//        return "success";
//    }

    @Autowired
    IDataService iDataService;

    @GetMapping(value = "test")
    public List<DataBuildingDto> test(){

//        Param.ValType.Special_V, Param.RunDataType.RUN_RTREAL
        List<String> buildings = new ArrayList<>();
        buildings.add("SGJZ00000157");
        List<String> pointType = new ArrayList<>();
        pointType.add(PointTypeEnum.WATER_VOLUME.getId());
        pointType.add(PointTypeEnum.FLOW.getId());
        return  iDataService.getSpecialDataRunRtreal(buildings,pointType);
    }
//
//    @GetMapping(value = "testDay")
//    public List<DataBuildingDto> testDay(){
//        List<String> buildings = new ArrayList<>();
//        buildings.add("SGJZ00000157");
//        List<String> pointType = new ArrayList<>();
//        pointType.add(PointTypeEnum.WATER_VOLUME.getId());
//        pointType.add(PointTypeEnum.FLOW.getId());
//
//        Long sdt  = 1611158400000L;//2021-01-21 00:00:00
//        Long edt = 1611244800000L;//2021-01-22 00:00:00
//
//        return iDataService.getSpecialDataRunDataType(buildings,pointType,sdt,edt,Param.ValType.Special_AVGV,Param.RunDataType.RUN_DAY);
//    }

//    @GetMapping(value = "testDay")
//    public List<DataBuildingDto> testDay(){
//        List<String> buildings = new ArrayList<>();
//        buildings.add("SGJZ00000157");
//        buildings.add("SGJZ00000169");
//        buildings.add("SGJZ00000170");
//        buildings.add("SGJZ00000183");
//        List<String> pointType = new ArrayList<>();
//        pointType.add(PointTypeEnum.WATER_VOLUME.getId());
//        pointType.add(PointTypeEnum.FLOW.getId());
//
//        Long sdt  = 1611158400000L;//2021-01-21 00:00:00
//        Long edt = 1611244800000L;//2021-01-22 00:00:00
//
//        return iDataService.getSpecialDataRunDataType(buildings,pointType,sdt,edt,Param.ValType.Special_AVGV,Param.RunDataType.RUN_DAY,null);
//    }

    @GetMapping(value = "testMonth")
    public List<DataSetBuildingDto> testMonth(){
        List<String> buildings = new ArrayList<>();
        buildings.add("SGJZ00000157");
        buildings.add("SGJZ000001572");
        buildings.add("SGJZ00000183");
        List<String> pointType = new ArrayList<>();
        pointType.add(PointTypeEnum.WATER_VOLUME.getId());
        pointType.add(PointTypeEnum.FLOW.getId());

        Long sdt  = 1609430400000L;//2021-01-01 00:00:00
        Long edt = 1617033600000L;//2021-03-30 00:00:00
        return iDataService.getSpecialDataSet(buildings,pointType,sdt,edt, Param.ValType.Special_AVGV,Param.RunDataType.RUN_MONTH);
    }

    public static void main(String[] args) throws ParseException {
        String year = "2014";
        Calendar bt = Calendar.getInstance();
        bt.set(2017,1,1,0,0,0);
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//        sdf1.
//        log.info("aaaa"+bt.getTime().toString());
//        bt.getTime()
//        DateUtils.convertTimeToDate()
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.parse("2014"+"-01-01 00:00:00");
        simpleDateFormat.parse("2014"+"-01-01 00:00:00").getTime();
        log.info("aaaa"+String.valueOf(simpleDateFormat.parse("2014"+"-1-1 00:00:00").getTime()));

        long beforeTime = 1613836800000L;
        long afterTime = 1614528000000L;
         long a = (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
//        findDates(DateUtils.convertTimeToDate(beforeTime),DateUtils.convertTimeToDate(afterTime));
         log.info(String.valueOf(a));
    }

//    public static List<Date> findDates(Date dBegin, Date dEnd) {
//        List lDate = new ArrayList();
//        lDate.add(dBegin);
//        Calendar calBegin = Calendar.getInstance();
//        // 使用给定的 Date 设置此 Calendar 的时间
//        calBegin.setTime(dBegin);
//        Calendar calEnd = Calendar.getInstance();
//        // 使用给定的 Date 设置此 Calendar 的时间
//        calEnd.setTime(dEnd);
//        // 测试此日期是否在指定日期之后
//        while (dEnd.after(calBegin.getTime())) {
//            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
//            calBegin.add(Calendar.DAY_OF_MONTH, 1);
//            lDate.add(calBegin.getTime());
//        }
//        return lDate;
//    }
}
