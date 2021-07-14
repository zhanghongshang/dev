package com.nari.slsd.msrv.waterdiversion.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.waterdiversion.model.po.Demo;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoMapper extends BaseMapper<Demo> {

}
