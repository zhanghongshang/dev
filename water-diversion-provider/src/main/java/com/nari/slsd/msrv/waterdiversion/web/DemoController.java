package com.nari.slsd.msrv.waterdiversion.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.waterdiversion.config.annotations.ResponseResult;
import com.nari.slsd.msrv.waterdiversion.interfaces.IDemoService;
import com.nari.slsd.msrv.waterdiversion.model.dto.DemoDTO;
import com.nari.slsd.msrv.waterdiversion.model.po.Demo;
import com.nari.slsd.msrv.waterdiversion.model.vo.DemoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @description: 控制层、服务接口
 * @author: Created by ZHD
 * @date: 2021/4/1 15:56
 * @return:
 */
@RestController
@RequestMapping("api/demo")
@ResponseResult //统一返回处理注解
public class DemoController {

    @Autowired
   private  IDemoService demoService;
    @PostMapping(produces = "application/json;charset=UTF-8")
    public String addDemo(@RequestBody DemoDTO demoDTO){
        demoService.save (demoDTO);
        return "success";
    }
    @DeleteMapping(value = "/{id}")
    public String deleteDemo(@PathVariable("id") String id){
        demoService.delete (id);
        return "success";
    }
    @GetMapping(value = "/{id}")
    public DemoVO findById(@PathVariable("id") Long id) {
        return demoService.findById (id);
    }
    @GetMapping(value = "/page")
    public DataTableVO page(@RequestParam("start") Integer start,
                           @RequestParam("length") Integer length){
        //DemoDTO searchData=new DemoDTO();
        PageModel pageModel = PageModel.builder()
                .start(start)
                .pageSize(length)
                //.searchData(searchData)
                .build();
        DataTableVO datatable=demoService.selectPage(pageModel);
        //IPage<Demo> page = new Page<>(start, length);//参数一是当前页，参数二是每页个数
        //page = demoService.selectPage((Page<Demo>) page);
        //List<Demo> list = page.getRecords();
        return datatable;
    }

}
