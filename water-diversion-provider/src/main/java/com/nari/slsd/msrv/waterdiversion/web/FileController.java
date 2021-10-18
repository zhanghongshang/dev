package com.nari.slsd.msrv.waterdiversion.web;

import com.nari.slsd.msrv.common.exception.TransactionException;
import com.nari.slsd.msrv.common.ext.enums.CodeEnum;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ResponseResult;
import com.nari.slsd.msrv.waterdiversion.config.excel.WaterPlanYearModel;
import com.nari.slsd.msrv.waterdiversion.config.excel.WrDayInputInDayModel;
import com.nari.slsd.msrv.waterdiversion.config.excel.WrDayInputInMonthModel;
import com.nari.slsd.msrv.waterdiversion.config.listener.WaterPlanYearDataListener;
import com.nari.slsd.msrv.waterdiversion.config.listener.WrDayInputInDayListener;
import com.nari.slsd.msrv.waterdiversion.config.listener.WrDayInputInMonthListener;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterPlanFullFillInYearService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDayInMonthInputService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDayInputService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrPlanGenerateMonthService;
import com.nari.slsd.msrv.waterdiversion.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @title
 * @description 文件操作
 * @author bigb
 * @updateTime 2021/09/01 23:07
 * @throws
 */
@RestController
@RequestMapping("api/file-operate")
@Slf4j
@RefreshScope
public class FileController {

    private static final int HEAD_ROW_NUM = 2;

    @Autowired
    private IWaterPlanFullFillInYearService iWaterPlanFullFillInYearService;

    @Autowired
    private IWrPlanGenerateMonthService wrPlanGenerateMonthService;

    @Autowired
    private IWrDayInMonthInputService wrDayInMonthInputService;

    @Autowired
    private IWrDayInputService wrDayInputService;

    /**
     * 实现单文件上传
     * userId: 操作人
     * importType: 0-全量导入(管理站人员) 1-部分导入(用水单位人员)
     * */
    @ResponseResult
    @PostMapping(value = "/fileUpload" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void fileUpload(@RequestPart(value = "file") MultipartFile multfile ,
                           @RequestParam(value = "userId") String userId) {
        if (null == multfile || multfile.isEmpty()) {
            throw new TransactionException(CodeEnum.NO_PARAM, "上传文件流为空");
        }
        try{
            long startTime = System.currentTimeMillis();
            WaterPlanYearDataListener dataListener = WaterPlanYearDataListener.getInstance();
            // 获取文件名
            String fileName = multfile.getOriginalFilename();
            // 获取文件后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            //取文件名前4位作为年份
            String year = String.valueOf(LocalDate.now().getYear());
            String fileNameExt = StringUtils.defaultString(fileName,"").trim();
            if(fileNameExt.length() > 4){
                String yearExt = fileNameExt.substring(0,4);
                if(StringUtils.isNumeric(yearExt)){
                    year = yearExt;
                }
            }
            // 用uuid作为文件名，防止生成的临时文件重复
            final File excelFile = File.createTempFile(IDGenerator.getId(), suffix);
            // MultipartFile to File
            multfile.transferTo(excelFile);
            ExcelUtil.readExcelForComplexHead(excelFile,HEAD_ROW_NUM,dataListener);
            List<WaterPlanYearModel> parseResult = dataListener.getParseResult();
            List<WaterPlanYearModel> realList = parseResult.stream().filter(e -> StringUtils.isNotEmpty(e.getBuildingCode())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(realList)){
                return;
            }
            iWaterPlanFullFillInYearService.planFullFill(realList,userId,year);
            long endTime = System.currentTimeMillis();
            log.info("全量导入年计划.耗时:{}秒",(endTime - startTime) / 1000);
        }catch (IOException e){
            throw new TransactionException(CodeEnum.ERROR, "上传文件流为空");
        }
    }

    /**
     * 日水情录入日数据方式导入
     * */
    @ResponseResult
    @PostMapping(value = "/fileUpload-day-input" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void fileUploadForDayInput(@RequestPart(value = "file") MultipartFile multfile ,
                           @RequestParam(value = "userId") String userId) {
        if (null == multfile || multfile.isEmpty()) {
            throw new TransactionException(CodeEnum.NO_PARAM, "上传文件流为空");
        }
        try{
            long startTime = System.currentTimeMillis();
            WrDayInputInDayListener dataListener = WrDayInputInDayListener.getInstance();
            // 获取文件名
            String fileName = multfile.getOriginalFilename();
            // 获取文件后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            //取文件名前6位作为年份和月份
            String year = String.valueOf(LocalDate.now().getYear());
            String month = String.valueOf(LocalDate.now().getMonth());
            String fileNameExt = StringUtils.defaultString(fileName,"").trim();
            if(fileNameExt.length() > 6){
                String yearExt = fileNameExt.substring(0,4);
                if(StringUtils.isNumeric(yearExt)){
                    year = yearExt;
                }
                String monthExt = fileNameExt.substring(4,6);
                if(StringUtils.isNumeric(monthExt)){
                    month = monthExt;
                }
            }
            // 用uuid作为文件名，防止生成的临时文件重复
            final File excelFile = File.createTempFile(IDGenerator.getId(), suffix);
            // MultipartFile to File
            multfile.transferTo(excelFile);
            ExcelUtil.readExcelForComplexHead(excelFile,HEAD_ROW_NUM,dataListener);
            List<WrDayInputInDayModel> parseResult = dataListener.getParseResult();
            List<WrDayInputInDayModel> realList = parseResult.stream().filter(e -> StringUtils.isNotEmpty(e.getBuildingCode())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(realList)){
                return;
            }
            wrDayInputService.importInDayForDayInput(userId,year,month,realList);
            long endTime = System.currentTimeMillis();
            log.info("日水情录入日数据导入.耗时:{}秒",(endTime - startTime) / 1000);
        }catch (IOException e){
            throw new TransactionException(CodeEnum.ERROR, "上传文件流为空");
        }
    }

    /**
     * 月计划生成
     * */
    @ResponseResult
    @PostMapping(value = "/test-month-fill")
    public void fillMonthPlanTest() {
        long startTime = System.currentTimeMillis();
        wrPlanGenerateMonthService.autoGenerateMonthPlan();
        long endTime = System.currentTimeMillis();
        System.out.println("===============月计划生成耗时:"+(endTime - startTime) / 1000 + "秒================");
    }

    /**
     * 日水情月数据导入
     * */
    @ResponseResult
    @PostMapping(value = "/test-month-fill-day-input")
    public void wrDayInputFillTest(@RequestParam(value = "userId") String userId,
                                   @RequestParam(value = "year") String year) {
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(year)){
            throw new TransactionException(CodeEnum.NO_PARAM,"请传入操作人id或导入年份！");
        }
        String filePath = "C:\\Users\\86180\\Desktop\\日水情录入模板-月.xlsx";
        WrDayInputInMonthListener instance = WrDayInputInMonthListener.getInstance();
        ExcelUtil.readExcelForComplexHead(new File(filePath),2,instance);
        List<WrDayInputInMonthModel> parseResult = instance.getParseResult();
        List<WrDayInputInMonthModel> realList = parseResult.stream().filter(e -> StringUtils.isNotEmpty(e.getBuildingCode())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(realList)){
            return;
        }
        wrDayInMonthInputService.importInMonthForDayInput(userId,year,realList);
    }

}
