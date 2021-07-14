package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.model.PageModel;
import com.nari.slsd.msrv.common.model.ResultCode;
import com.nari.slsd.msrv.common.model.ResultModel;
import com.nari.slsd.msrv.common.utils.DataTableConverter;
import com.nari.slsd.msrv.common.utils.ResultModelUtils;
import com.nari.slsd.msrv.waterdiversion.mapper.DemoMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IDemoService;
import com.nari.slsd.msrv.waterdiversion.model.dto.DemoDTO;
import com.nari.slsd.msrv.waterdiversion.model.po.Demo;
import com.nari.slsd.msrv.waterdiversion.model.vo.DemoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: DemoServiceImpl
 * @Description:  XXX功能接口实现
 * @Author: sk
 * @Date: 2020/4/13 15:44
 * @Version: 1.0
 * @Remark:
 **/
@Slf4j
@Service
public class DemoServiceImpl implements IDemoService {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    DemoMapper demoMapper;

    @Override
    public void save(DemoDTO demoDTO) {
        demoMapper.insert (convert2DO (demoDTO));
    }

    @Override
    public void update(DemoDTO demoDTO) {
        demoMapper.updateById(convert2DO (demoDTO));
    }

    @Override
    public void delete(String id) {
        demoMapper.deleteById (Long.parseLong (id));
    }

    @Override
    public DemoVO findById(String id) {
         return convert2VO (demoMapper.selectById(id));
    }
    public IPage<Demo> selectPage(Page<Demo> page) {
        page = demoMapper.selectPage(page,null);
        List<Demo> list = page.getRecords();
        return demoMapper.selectPage(page,null);
    }
    @Override
    public void batchInsert(List<Demo> beanList) {
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);
        DemoMapper mapper = session.getMapper(DemoMapper.class);
        try {
            for (int i = 0;i<beanList.size();i++) {
                mapper.insert(beanList.get(i));
                if(i%1000==999 || i==beanList.size()-1) {
                    session.commit();
                    session.clearCache();
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            session.rollback();
        }finally {
            session.close();
        }
    }
    @Override
    public DataTableVO selectPage(PageModel pageModel) {
        IPage<Demo> page = new Page<>(pageModel.getStart(), pageModel.getPageSize());
        page = demoMapper.selectPage(page,null);
        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setData(page.getRecords());
        return dataTableVO;
    }
    protected Demo convert2DO(DemoDTO demoDTO) {
        Demo demo = new Demo();
        BeanUtils.copyProperties (demoDTO, demo);
        demo.setId (Long.parseLong (demoDTO.getId ()));
        return demo;
    }

    protected DemoVO convert2VO(Demo demo) {
        DemoVO demoVO= new DemoVO ();
        BeanUtils.copyProperties (demo,demoVO);
        demoVO.setId (String.valueOf (demo.getId ()));
        return demoVO;
    }


}
