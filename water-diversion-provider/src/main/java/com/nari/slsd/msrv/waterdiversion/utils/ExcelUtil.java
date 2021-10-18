package com.nari.slsd.msrv.waterdiversion.utils;

import cn.hutool.core.io.FileTypeUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.nari.slsd.msrv.common.exception.TransactionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author bigb
 * @version 1.0.0
 * @ClassName ExcelUtil
 * @Description excel读写
 * @createTime 2021年08月30日
 */
@Slf4j
@Component
public class ExcelUtil {

    /**
     * @title readExcelForComplexHead
     * @description 复杂表头excel读取，默认Sheet1
     * @author bigb
     * @param: file
     * @param: sourceClazz
     * @updateTime 2021/8/31 22:11
     * @throws
     */
    public static void readExcelForComplexHead(File file , int headRowNum , AnalysisEventListener dataListener) {
        ExcelReader excelReader = null;
        try {
            String fileType = "." + FileTypeUtil.getType(file);
            ExcelReaderBuilder readerBuilder = EasyExcel.read(file)
                    .extraRead(CellExtraTypeEnum.MERGE)
                    .registerReadListener(dataListener)
                    .ignoreEmptyRow(true);
            //excel扩展名
            readerBuilder.excelType(ExcelTypeEnum.XLSX);
            if(ExcelTypeEnum.XLS.getValue().equalsIgnoreCase(fileType)){
                readerBuilder.excelType(ExcelTypeEnum.XLS);
            }
            //表头
            if(headRowNum > 0){
                readerBuilder.headRowNumber(headRowNum);
            }
            excelReader = readerBuilder.build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
        } catch (Exception e) {
            log.error("readExcelForComplexHead fail , error is {}", e);
            throw new TransactionException("excel读取失败");
        } finally {
            if (null != excelReader) {
                excelReader.finish();
            }
        }
    }
}
