package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.BeanUtils;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWaterBuildingManagerService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrBuildingAndDiversionService;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrDiversionPortService;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WaterBuildingManagerMapper;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrDiversionPortMapper;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrBuildingAndDiversion;
import com.nari.slsd.msrv.waterdiversion.model.po.WaterBuildingManager;
import com.nari.slsd.msrv.waterdiversion.model.po.WrDiversionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 测站-引水口管理 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-08-03
 */
@Service
@Slf4j
public class WrBuildingAndDiversionServiceImpl implements IWrBuildingAndDiversionService {

    @Resource
    IWrDiversionPortService wrDiversionPortService;

    @Resource
    IWaterBuildingManagerService waterBuildingManagerService;

    @Resource
    WrDiversionPortMapper wrDiversionPortMapper;

    @Resource
    WaterBuildingManagerMapper waterBuildingManagerMapper;


    @Resource
    TransactionTemplate transactionTemplate;


    @Override
    public DataTableVO getBuildingAndDiversionPage(Integer start, Integer length, Integer buildingType, String waterUnitId, String mngUnitId) {
        /**
         * 先分页查水工建筑表
         * 按类型，上级用水单位，管理单位筛选
         */
        QueryWrapper<WaterBuildingManager> pageWrapper = new QueryWrapper<>();
        if (buildingType != null) {
            pageWrapper.eq("BUILDING_TYPE", buildingType);
        }
        if (StringUtils.isNotEmpty(waterUnitId)) {
            pageWrapper.eq("WATER_UNIT_ID", waterUnitId);
        }
        if (StringUtils.isNotEmpty(waterUnitId)) {
            pageWrapper.eq("MNG_UNIT_ID", mngUnitId);
        }
        IPage<WaterBuildingManager> page = new Page<>(start, length);
        waterBuildingManagerService.page(page, pageWrapper);
        List<WaterBuildingManager> buildingList = page.getRecords();

        /**
         * 如果左表为空，直接返回
         */
        if (buildingList == null || buildingList.size() == 0) {
            return new DataTableVO();
        }

        /**
         *  测站-引水口管理id
         */
        List<String> buildingAndDiversionIds = buildingList.stream().map(building -> building.getId()).collect(Collectors.toList());

        /**
         *  根据id去查其他数据
         */
        QueryWrapper<WrDiversionPort> diversionPortWrapper = new QueryWrapper<>();
        diversionPortWrapper.in("ID", buildingAndDiversionIds);
        List<WrDiversionPort> wrDiversionPortList = wrDiversionPortService.list(diversionPortWrapper);
        Map<String, WrDiversionPort> wrDiversionPortMap = wrDiversionPortList.stream().collect(Collectors.toMap(po -> po.getId(), po -> po));

        /**
         * 整合成测站-引水口List
         */

        List<WrBuildingAndDiversion> voList = buildingList.stream().map(po -> {
            String id = po.getId();
            WrDiversionPort wrDiversionPort = wrDiversionPortMap.getOrDefault(id, null);
            WrBuildingAndDiversion wrBuildingAndDiversion = convert2WrBuildingAndDiversion(po, wrDiversionPort);

            return wrBuildingAndDiversion;
        }).collect(Collectors.toList());

        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setData(voList);
        return dataTableVO;
    }


    /**
     * TODO 增加事务管理后会找不到表？
     * 无效的表或视图名[WATER_BUILDING_MANAGER]
     *
     * @param dto
     */
//    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void updateBuildingAndDiversion(WrBuildingAndDiversion dto) {

        /**
         * 分割为两个表
         * 修改左表水工建筑物表
         * 修改右表 测站-饮水口管理
         * 记录不存在新增。存在则修改
         */

        WaterBuildingManager waterBuildingManager = convert2WaterBuildingManager(dto);
        WrDiversionPort wrDiversionPort = convert2WrDiversionPort(dto);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                waterBuildingManagerMapper.updateById(waterBuildingManager);
                wrDiversionPortService.saveOrUpdate(wrDiversionPort);
            }
        });
    }


    /**
     * 合并成测站-引水口伪表
     *
     * @param waterBuildingManager
     * @param wrDiversionPort
     * @return
     */
    public static WrBuildingAndDiversion convert2WrBuildingAndDiversion(WaterBuildingManager waterBuildingManager, WrDiversionPort wrDiversionPort) {
        WrBuildingAndDiversion wrBuildingAndDiversion = new WrBuildingAndDiversion();
        if (waterBuildingManager == null) {
            return wrBuildingAndDiversion;
        }
        BeanUtils.copyProperties(waterBuildingManager, wrBuildingAndDiversion);
        if (wrDiversionPort != null) {
            BeanUtils.copyProperties(wrDiversionPort, wrBuildingAndDiversion);
        }
        return wrBuildingAndDiversion;
    }


    /**
     * 转水工建筑表
     *
     * @param wrBuildingAndDiversion
     * @return
     */
    public static WaterBuildingManager convert2WaterBuildingManager(WrBuildingAndDiversion wrBuildingAndDiversion) {
        WaterBuildingManager waterBuildingManager = new WaterBuildingManager();
        BeanUtils.copyProperties(wrBuildingAndDiversion, waterBuildingManager);
        return waterBuildingManager;
    }

    /**
     * 转测站-引水口管理右表
     *
     * @param wrBuildingAndDiversion
     * @return
     */
    public static WrDiversionPort convert2WrDiversionPort(WrBuildingAndDiversion wrBuildingAndDiversion) {
        WrDiversionPort wrDiversionPort = new WrDiversionPort();
        BeanUtils.copyProperties(wrBuildingAndDiversion, wrDiversionPort);
        return wrDiversionPort;
    }


}
