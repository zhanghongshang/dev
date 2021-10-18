package com.nari.slsd.msrv.waterdiversion.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @title
 * @description 预警通知生成
 * @author bigb
 * @updateTime 2021/9/14 13:54
 * @throws
 */
@Service
@Slf4j
public class WrWarningNoticeGenerateServiceImpl {

    private void autoGenerateWarningNotice(){
        //所有指令按照管理站维度编制
        //1、实引水量已超出旬计划,生成通知
        //2、旬实际使用超计划80%,生成预警通知
        //3、低于80%,但是实引水量超出截止时间旬计划,生成通知
        //截止到当前的累计实引水量

    }
}
