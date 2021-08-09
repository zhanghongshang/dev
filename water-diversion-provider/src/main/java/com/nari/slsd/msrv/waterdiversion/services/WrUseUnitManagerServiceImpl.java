package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.init.service.InitModelCacheImpl;
import com.nari.slsd.msrv.waterdiversion.model.dto.PersonTransDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrUseUnitManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.po.WrUseUnitManager;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrUseUnitManagerMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitManagerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.model.po.WrUseUnitPerson;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 用水单位管理 服务实现类
 * </p>
 *
 * @author reset kalar
 * @since 2021-07-29
 */
@Slf4j
@Service
public class WrUseUnitManagerServiceImpl extends ServiceImpl<WrUseUnitManagerMapper, WrUseUnitManager> implements IWrUseUnitManagerService {

    @Resource
    WrUseUnitPersonServiceImpl personService;

    @Resource
    IModelCacheService cacheService;

    @Resource
    InitModelCacheImpl initModelCache;

    @Resource
    TransactionTemplate transactionTemplate;

    @Override
    public void addWaterUseUnit(WrUseUnitManagerDTO dto) {
        /**
         * 新增时要先新增数据库记录再生成缓存
         *
         * 新增用水单位和人员
         *
         * Transactional注解内部调用不走代理，不生效，所以手动启用事务
         */
        WrUseUnitManager po = transactionTemplate.execute(transactionStatus -> addOneWaterUseUnit(dto));

        /**
         * 修改Redis缓存的树模型
         * 如果PID为-1 是新的树，直接存入redis
         * 如果PID不为-1 向上找到根节点，修改树结构
         */
        if (dto.getPid().equals("-1")) {
            initModelCache.updateWaterUseUnitTree(po.getId());
        } else {
            String rootId = getRootId(po.getId());
            initModelCache.updateWaterUseUnitTree(rootId);
        }
    }

    /**
     * 新增用水单位
     * Transactional注解内部调用不走代理，不生效
     *
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = {Exception.class})
    public WrUseUnitManager addOneWaterUseUnit(WrUseUnitManagerDTO dto) {
        /**
         * 用水单位dto转po 入数据库
         */
        WrUseUnitManager po = convert2DO(dto);
        baseMapper.insert(po);
        /**
         * 如果用水单位的人员列表不为空，将人员存入用水单位人员表
         */
        List<PersonTransDTO> personDTOList = dto.getPersonList();

        if (personDTOList != null && personDTOList.size() != 0) {
            String unitId = po.getId();
            List<WrUseUnitPerson> personList = new ArrayList<>();
            personDTOList.forEach(personDTO -> {
                WrUseUnitPerson person = new WrUseUnitPerson();
                person.setId(IDGenerator.getId());
                person.setUserId(personDTO.getUserId());
                person.setUserType(personDTO.getUserType());
                person.setUnitId(unitId);
                personList.add(person);
            });
            personService.saveBatch(personList);
        }
        return po;
    }


    private String getRootId(String id) {
        WrUseUnitManager po = baseMapper.selectById(id);
        if (po.getPid().equals("-1")) {
            return po.getId();
        } else {
            return getRootId(po.getPid());
        }
    }

    /**
     * 更新用水单位信息
     * TODO 用水单位人员是否可能为空？是否要做类型判断？
     *
     * @param dto
     */
    @Override
    public void updateWaterUseUnit(WrUseUnitManagerDTO dto) {
        /**
         * 暂存旧的PID
         * 旧的根节点
         */
        String oldPid = baseMapper.selectById(dto.getId()).getPid();
        String oldRootId = getRootId(dto.getId());
        /**
         * 修改用水单位信息
         */
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                updateOneWaterUseUnit(dto);
            }
        });

        /**
         * 新的根节点
         */
        String newRootId = getRootId(dto.getId());

        /**
         * 如果父级用水单位修改
         *      可能要修改两颗树
         * 如果用水单位名称修改
         *      修改本树
         */
        if (StringUtils.isNotEmpty(dto.getPid())) {
            /**
             * 如果父节点改变
             */
            if (oldPid.equals("-1")) {
                /**
                 * 原来是一级单位，删除旧的树
                 */
                cacheService.delWaterUseUnitTree(dto.getId());
            } else {
                /**
                 * 不是一级单位，找到根节点，更新原来所在树
                 */
                initModelCache.updateWaterUseUnitTree(oldRootId);
            }
            /**
             * 如果父节点更新后还在旧树，无需其他操作
             * 如果父节点更新后不在旧树，要更新新树缓存
             */
            if (!oldRootId.equals(newRootId)) {
                initModelCache.updateWaterUseUnitTree(newRootId);
            }

        } else if (StringUtils.isNotEmpty(dto.getUnitName())) {
            /**
             * 父节点未改变，只改变了节点名
             */
            String rootId = getRootId(dto.getId());
            initModelCache.updateWaterUseUnitTree(rootId);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateOneWaterUseUnit(WrUseUnitManagerDTO dto) {
        WrUseUnitManager po = convert2DO(dto);
        baseMapper.updateById(po);

        /**
         * 更新用水单位的人员列表
         * 过滤创建人
         */
        List<PersonTransDTO> personDTOList = dto.getPersonList().stream().filter(personTrans -> personTrans.getUserType() != 1).collect(Collectors.toList());

        if (personDTOList != null && personDTOList.size() != 0) {
            /**
             * 属于该单位的人员列表
             * 过滤创建人
             */
            String unitId = po.getId();
            QueryWrapper<WrUseUnitPerson> wrapper = new QueryWrapper();
            wrapper.eq("UNIT_ID", unitId);
            wrapper.ne("USER_TYPE", 1);
            List<WrUseUnitPerson> personOldList = personService.list(wrapper);
            /**
             * ID TYPE做伪双主键
             */
            List<PersonTransDTO> personTransOldList = personOldList.stream().map(person -> new PersonTransDTO(person.getUserId(), person.getUserType())).collect(Collectors.toList());
            Map<PersonTransDTO, WrUseUnitPerson> personTransOldMap = personOldList.stream().collect(Collectors.toMap(person -> new PersonTransDTO(person.getUserId(), person.getUserType()), person -> person));

            /**
             * 过滤出要删除的人员
             * 用伪双主键比较
             */
            List<WrUseUnitPerson> personDelList = personTransOldList.stream().filter(personTrans -> personDTOList.contains(personTrans) ? false : true).map(personTrans -> personTransOldMap.get(personTrans)).collect(Collectors.toList());
            /**
             * 获取要删除人员的主键ID
             * 删除对应人员
             */
            List<String> personPKDelList = personDelList.stream().map(person -> person.getId()).collect(Collectors.toList());
            personService.removeByIds(personPKDelList);

            /**
             * 过滤出要新增的人员
             */
            List<PersonTransDTO> personTransAddList = personDTOList.stream().filter(personTrans -> personTransOldList.contains(personTrans) ? false : true).collect(Collectors.toList());

            List<WrUseUnitPerson> personAddList = new ArrayList<>();
            personTransAddList.forEach(personTrans -> {
                WrUseUnitPerson person = new WrUseUnitPerson();
                person.setId(IDGenerator.getId());
                person.setUserId(personTrans.getUserId());
                person.setUserType(personTrans.getUserType());
                person.setUnitId(unitId);
                personAddList.add(person);
            });
            personService.saveBatch(personAddList);
        }
    }

    @Override
    public void deleteWaterUseUnitById(String id) {
        /**
         * 如果是一级单位，先删除缓存树，再删除数据库
         *
         * 如果不是，先删除数据库记录，再更新缓存树
         */
        String pid = baseMapper.selectById(id).getPid();

        if (pid.equals("-1")) {
            cacheService.delWaterUseUnitTree(id);
            /**
             * 递归删除用水单位节点及所有子节点，包含人员关系
             */
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    deleteOneWaterUseUnitById(id);
                }
            });
        } else {
            String rootId = getRootId(id);
            /**
             * 递归删除用水单位节点及所有子节点，包含人员关系
             */
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    deleteOneWaterUseUnitById(id);
                }
            });
            initModelCache.updateWaterUseUnitTree(rootId);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteOneWaterUseUnitById(String id) {
        /**
         * 判断是否存在子用水单位
         */
        QueryWrapper<WrUseUnitManager> managerWrapper = new QueryWrapper<>();
        managerWrapper.eq("PID", id);
        List<WrUseUnitManager> poList = baseMapper.selectList(managerWrapper);
        if (poList != null && poList.size() != 0) {
            poList.forEach(sonPo -> deleteOneWaterUseUnitById(sonPo.getId()));
        }

        /**
         * 先删除属于该用水单位的人员
         */
        QueryWrapper<WrUseUnitPerson> wrapper = new QueryWrapper<>();
        wrapper.eq("UNIT_ID", id);
        personService.remove(wrapper);
        /**
         * 再删除该用水单位
         */
        baseMapper.deleteById(id);
    }

    @Override
    public List<WrUseUnitManagerVO> getAllWaterUseUnitList() {
        /**
         * 查所有用水单位
         */
        List<WrUseUnitManager> managerList = baseMapper.selectList(null);
        /**
         * 查这些用水单位下所有人员
         */
        List<String> idList = managerList.stream().map(manager -> manager.getId()).collect(Collectors.toList());
        QueryWrapper<WrUseUnitPerson> wrapper = new QueryWrapper<>();
        wrapper.in("UNIT_ID", idList);
        List<WrUseUnitPerson> personList = personService.list(wrapper);
        /**
         * 整合用水单位的人员
         */
        List<WrUseUnitManagerVO> voList = convert2VOList(managerList);
        Map<String, WrUseUnitManagerVO> voMap = voList.stream().collect(Collectors.toMap(vo -> vo.getId(), vo -> vo));

        personList.forEach(person -> {
            if (voMap.containsKey(person.getUnitId())) {
                WrUseUnitManagerVO vo = voMap.get(person.getUnitId());
                PersonTransDTO personTrans = new PersonTransDTO();
                personTrans.setUserId(person.getUserId());
                personTrans.setUserType(person.getUserType());
                vo.getPersonList().add(personTrans);
            }
        });
        return voMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public DataTableVO getWaterUseUnitPage(Integer start, Integer length, String pid, Integer state) {
        QueryWrapper<WrUseUnitManager> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(pid)) {
            wrapper.eq("PID", pid);
        }
        if (state != null) {
            wrapper.eq("STATE", state);
        }
        wrapper.orderByAsc("CODE");
        IPage<WrUseUnitManager> page = new Page<>(start, length);

        baseMapper.selectPage(page, wrapper);
        List<WrUseUnitManager> managerList = page.getRecords();
        /**
         * 查询上级用水单位名称
         */
        List<String> pIds = managerList.stream().map(po -> po.getPid()).distinct().collect(Collectors.toList());
        List<WrUseUnitManager> parents = baseMapper.selectBatchIds(pIds);
        Map<String, String> parentMap = parents.stream().collect(Collectors.toMap(p -> p.getId(), p -> p.getUnitName()));

        /**
         * 无数据直接返回
         */
        if (managerList == null || managerList.size() == 0) {
            return new DataTableVO();
        }

        /**
         * 查这些用水单位下所有人员
         */
        List<String> idList = managerList.stream().map(manager -> manager.getId()).collect(Collectors.toList());
        QueryWrapper<WrUseUnitPerson> personWrapper = new QueryWrapper<>();
        personWrapper.in("UNIT_ID", idList);
        List<WrUseUnitPerson> personList = personService.list(personWrapper);
        /**
         * 整合用水单位的人员
         */
        List<WrUseUnitManagerVO> voList = convert2VOList(managerList);
        voList.forEach(vo -> {
            if (parentMap.containsKey(vo.getPid())) {
                vo.setPunitName(parentMap.get(vo.getPid()));
            }
        });
        Map<String, WrUseUnitManagerVO> voMap = voList.stream().collect(Collectors.toMap(vo -> vo.getId(), vo -> vo));

        personList.forEach(person ->

        {
            if (voMap.containsKey(person.getUnitId())) {
                WrUseUnitManagerVO vo = voMap.get(person.getUnitId());
                PersonTransDTO personTrans = new PersonTransDTO();
                personTrans.setUserId(person.getUserId());
                personTrans.setUserType(person.getUserType());
                vo.getPersonList().add(personTrans);
            }
        });
        voList = voMap.values().

                stream().

                collect(Collectors.toList());

        DataTableVO dataTableVO = new DataTableVO();
        dataTableVO.setRecordsFiltered(page.getTotal());
        dataTableVO.setRecordsTotal(page.getTotal());
        dataTableVO.setData(voList);
        return dataTableVO;
    }

    private WrUseUnitManager convert2DO(WrUseUnitManagerDTO dto) {
        WrUseUnitManager po = new WrUseUnitManager();
        BeanUtils.copyProperties(dto, po);
        if (StringUtils.isEmpty(dto.getId())) {
            po.setId(IDGenerator.getId());
        }
        if (dto.getHousesTime() != null) {
            po.setHousesTime(DateUtils.convertTimeToDate(dto.getHousesTime()));
        }

        return po;
    }

    @Override
    public List<WrUseUnitNode> getTreeFromCacheByIds(List<String> ids) {
        List<WrUseUnitNode> nodeList = new ArrayList<>();
        ids.forEach(id -> {
            WrUseUnitNode node = getTreeFromCacheById(id);
            if (node != null) {
                nodeList.add(node);
            }
        });
        return nodeList;
    }


    @Override
    public List<WrUseUnitNode> getAllTreeFromCacheByIds() {
        QueryWrapper<WrUseUnitManager> wrapper = new QueryWrapper();
        wrapper.eq("PID", "-1");
        List<WrUseUnitManager> poList = baseMapper.selectList(wrapper);

        List<WrUseUnitNode> nodeList = new ArrayList<>();
        poList.forEach(po -> {
            WrUseUnitNode node = getTreeFromCacheById(po.getId());
            if (node != null) {
                nodeList.add(node);
            }
        });
        return nodeList;
    }

    @Override
    public List<String> getUnitIdsByPid(String pid) {
        QueryWrapper<WrUseUnitManager> wrapper = new QueryWrapper();
        wrapper.eq("PID", pid);
        List<WrUseUnitManager> poList = baseMapper.selectList(wrapper);
        return poList.stream().map(po -> po.getId()).collect(Collectors.toList());
    }


    @Override
    public WrUseUnitNode getTreeFromCacheById(String id) {
        return cacheService.getWaterUseUnitTree(id);
    }

    @Override
    public Boolean checkUniqueCode(String code) {
        QueryWrapper<WrUseUnitManager> wrapper = new QueryWrapper();
        wrapper.eq("CODE", code);
        Integer count = baseMapper.selectCount(wrapper);
        return count == 0 ? true : false;
    }


    public static WrUseUnitManagerVO convert2VO(WrUseUnitManager po) {
        WrUseUnitManagerVO vo = new WrUseUnitManagerVO();
        BeanUtils.copyProperties(po, vo);
        if (po.getHousesTime() != null) {
            vo.setHousesTime(DateUtils.convertDateToLong(po.getHousesTime()));
        }
        return vo;
    }

    public static List<WrUseUnitManagerVO> convert2VOList(List<WrUseUnitManager> poList) {
        List<WrUseUnitManagerVO> voList = new ArrayList<>();

        poList.forEach(po -> {
            WrUseUnitManagerVO vo = convert2VO(po);
            voList.add(vo);
        });

        return voList;
    }


}
