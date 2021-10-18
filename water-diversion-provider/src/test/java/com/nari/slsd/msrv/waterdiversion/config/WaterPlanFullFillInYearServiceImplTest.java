package com.nari.slsd.msrv.waterdiversion.config;

import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanGenerateMonthService;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName RedisUtilTest
 * @Description redis测试类
 * @createTime 2021年08月19日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WaterPlanFullFillInYearServiceImplTest {

    @Autowired
    private IWrPlanGenerateMonthService wrPlanGenerateMonthService;

    @Test
    public void batchSave(){
        //巴州
        //String unitId = "6e3aa4eabef9451591df3ebc032fa860";
        //库尔勒市
        //String unitId = "73b6fbb921eb43fe9b3eeef2e70f9b8d";
        //库尔勒市近郊厂
        String unitId = "7775c3d603f8410c919b942a8f0e7a67";
        List<WrBuildingAndDiversion> unitList = wrPlanGenerateMonthService.getAllWaterBuildingForAppointUseUnit(unitId);
        System.out.println(unitList);
    }

}
