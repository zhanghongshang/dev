package com.nari.slsd.msrv.waterdiversion.interfaces;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.waterdiversion.model.dto.DemoDTO;
import com.nari.slsd.msrv.waterdiversion.model.po.Demo;
import com.nari.slsd.msrv.waterdiversion.model.vo.DemoVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 接口处理
 * @author:
 * @date:
 * @return:
 */
public interface IDemoService extends IService<Demo> {
    void save(DemoDTO demoDTO);
    void update(DemoDTO demoDTO);
    void delete(String id);
    DemoVO findById(Long id);
    DataTableVO selectPage(PageModel pageModel);
    void batchInsert(List<Demo> beanList);
}
