package com.nari.slsd.msrv.waterdiversion.services;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nari.slsd.msrv.common.model.DataTableVO;
import com.nari.slsd.msrv.common.utils.DateUtils;
import com.nari.slsd.msrv.common.utils.IDGenerator;
import com.nari.slsd.msrv.common.utils.StringUtils;
import com.nari.slsd.msrv.waterdiversion.cache.interfaces.IModelCacheService;
import com.nari.slsd.msrv.waterdiversion.commons.TreeEnum;
import com.nari.slsd.msrv.waterdiversion.commons.WrUseUnitEnum;
import com.nari.slsd.msrv.waterdiversion.init.interfaces.IInitModelCache;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitPersonService;
import com.nari.slsd.msrv.waterdiversion.model.dto.PersonTransDTO;
import com.nari.slsd.msrv.waterdiversion.model.dto.PersonTransKey;
import com.nari.slsd.msrv.waterdiversion.model.dto.WrUseUnitManagerDTO;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitManager;
import com.nari.slsd.msrv.waterdiversion.mapper.primary.WrUseUnitManagerMapper;
import com.nari.slsd.msrv.waterdiversion.interfaces.IWrUseUnitManagerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nari.slsd.msrv.waterdiversion.model.primary.po.WrUseUnitPerson;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitManagerVO;
import com.nari.slsd.msrv.waterdiversion.model.vo.WrUseUnitNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    IWrUseUnitPersonService personService;

    @Resource
    IModelCacheService cacheService;

    @Resource
    IInitModelCache initModelCache;

    @Resource
    TransactionTemplate transactionTemplate;

    @Override
    public void saveWaterUseUnit(WrUseUnitManagerDTO dto) {
        //新增时要先新增数据库记录再生成缓存
        //新增用水单位和人员
        //Transactional注解内部调用不走代理，不生效，所以手动启用事务
        WrUseUnitManager po = transactionTemplate.execute(transactionStatus -> saveOneWaterUseUnit(dto));

        //修改Redis缓存的树模型
        //如果PID为-1 是新的树，直接存入redis
        //如果PID不为-1 向上找到根节点，修改树结构
        if (dto.getPid().equals(TreeEnum.WR_USE_UNIT_ROOT_PID)) {
            initModelCache.updateWaterUseUnitTree(po.getId());
        } else {
            String rootId = getRootId(po);
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
    public WrUseUnitManager saveOneWaterUseUnit(WrUseUnitManagerDTO dto) {
        //用水单位dto转po 入数据库
        WrUseUnitManager po = convert2DO(dto);
        //增加层级和路径
        //如果是一级节点 节点层级为1 路径为 id
        //不是一级节点 找他的父节点 层级+1 路径拼接pid/id
        if (po.getPid().equals(TreeEnum.WR_USE_UNIT_ROOT_PID)) {
            po.setUnitLevel(1);
            po.setPath(po.getId());
        } else {
            WrUseUnitManager parent = getById(po.getPid());
            Integer pLevel = parent.getUnitLevel();
            String pPath = parent.getPath();
            StringBuffer sb = new StringBuffer(pPath);
            sb.append("/").append(po.getId());
            po.setUnitLevel(pLevel + 1);
            po.setPath(sb.toString());
        }
        baseMapper.insert(po);
        //如果用水单位的人员列表不为空，将人员存入用水单位人员表
        List<PersonTransDTO> creatorDTOList = dto.getCreatorList();
        List<PersonTransDTO> managerDTOList = dto.getManagerList();
        List<PersonTransDTO> personDTOList = dto.getPersonList();

        List<WrUseUnitPerson> allPersonList = new ArrayList<>();

        if (creatorDTOList != null && creatorDTOList.size() != 0) {
            String unitId = po.getId();
            creatorDTOList.forEach(creatorDTO -> {
                WrUseUnitPerson person = new WrUseUnitPerson();
                person.setId(IDGenerator.getId());
                person.setUserId(creatorDTO.getUserId());
                if (creatorDTO.getUserType() != null) {
                    person.setUserType(creatorDTO.getUserType());
                } else {
                    person.setUserType(WrUseUnitEnum.USER_TYPE_CREATOR);
                }
                person.setUnitId(unitId);
                allPersonList.add(person);
            });
        }
        if (managerDTOList != null && managerDTOList.size() != 0) {
            String unitId = po.getId();
            managerDTOList.forEach(managerDTO -> {
                WrUseUnitPerson person = new WrUseUnitPerson();
                person.setId(IDGenerator.getId());
                person.setUserId(managerDTO.getUserId());
                if (managerDTO.getUserType() != null) {
                    person.setUserType(managerDTO.getUserType());
                } else {
                    person.setUserType(WrUseUnitEnum.USER_TYPE_MANAGER);
                }
                person.setUnitId(unitId);
                allPersonList.add(person);
            });
        }
        if (personDTOList != null && personDTOList.size() != 0) {
            String unitId = po.getId();
            personDTOList.forEach(personDTO -> {
                WrUseUnitPerson person = new WrUseUnitPerson();
                person.setId(IDGenerator.getId());
                person.setUserId(personDTO.getUserId());
                if (personDTO.getUserType() != null) {
                    person.setUserType(personDTO.getUserType());
                } else {
                    person.setUserType(WrUseUnitEnum.USER_TYPE_RELATED_PERSON);
                }
                person.setUnitId(unitId);
                allPersonList.add(person);
            });
        }

        personService.saveBatch(allPersonList);
        return po;
    }

    /**
     * 递归查根节点（弃用）
     *
     * @param id
     * @return
     */
    @Deprecated
    private String getRootId(String id) {
        WrUseUnitManager po = baseMapper.selectById(id);
        if (po.getPid().equals(TreeEnum.WR_USE_UNIT_ROOT_PID)) {
            return po.getId();
        } else {
            return getRootId(po.getPid());
        }
    }

    /**
     * 从字段解析根节点
     *
     * @param po
     * @return
     */
    @Override
    public String getRootId(WrUseUnitManager po) {
        String path = po.getPath();
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        String[] paths = path.split("/");
        return paths[0];
    }

    /**
     * 更新用水单位信息
     *
     * @param dto
     */
    @Override
    public void updateWaterUseUnit(WrUseUnitManagerDTO dto) {
        //暂存旧的PID
        //旧的根节点
        WrUseUnitManager oldPo = baseMapper.selectById(dto.getId());
        String oldPid = oldPo.getPid();
        String oldRootId = getRootId(oldPo);
        //修改用水单位信息
        WrUseUnitManager po = transactionTemplate.execute(transactionStatus -> updateOneWaterUseUnit(dto));

        //新的根节点
        String newRootId = getRootId(po);

        //如果父级用水单位修改 可能要修改两颗树
        //如果用水单位名称修改 修改本树
        if (StringUtils.isNotEmpty(dto.getPid())) {
            //如果父节点改变
            if (oldPid.equals(TreeEnum.WR_USE_UNIT_ROOT_PID)) {
                //原来是一级单位，删除旧的树
                cacheService.delWaterUseUnitTree(dto.getId());
            } else {
                //不是一级单位，找到根节点，更新原来所在树
                initModelCache.updateWaterUseUnitTree(oldRootId);
            }
            //如果父节点更新后还在旧树，无需其他操作
            //如果父节点更新后不在旧树，要更新新树缓存
            if (!oldRootId.equals(newRootId)) {
                initModelCache.updateWaterUseUnitTree(newRootId);
            }

        } else if (StringUtils.isNotEmpty(dto.getUnitName())) {
            //父节点未改变，只改变了节点名
            initModelCache.updateWaterUseUnitTree(newRootId);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public WrUseUnitManager updateOneWaterUseUnit(WrUseUnitManagerDTO dto) {
        WrUseUnitManager po = convert2DO(dto);
        //如果修改父节点 根据父节点修改level和path
        if (StringUtils.isNotEmpty(dto.getPid())) {
            if (po.getPid().equals(TreeEnum.WR_USE_UNIT_ROOT_PID)) {
                po.setUnitLevel(1);
                po.setPath(po.getId());
            } else {
                WrUseUnitManager parent = getById(po.getPid());
                Integer pLevel = parent.getUnitLevel();
                String pPath = parent.getPath();
                StringBuffer sb = new StringBuffer(pPath);
                sb.append("/").append(po.getId());
                po.setUnitLevel(pLevel + 1);
                po.setPath(sb.toString());
            }
        }
        baseMapper.updateById(po);

        //更新用水单位的人员列表
        //过滤创建人
        List<PersonTransDTO> managerDTOList = dto.getManagerList();
        List<PersonTransDTO> personDTOList = dto.getPersonList();
        List<PersonTransKey> allUpdatePerson = new ArrayList<>();

        if (managerDTOList != null && managerDTOList.size() != 0) {
            managerDTOList.forEach(managerDTO -> {
                PersonTransKey key = new PersonTransKey();
                key.setUserId(managerDTO.getUserId());
                if (managerDTO.getUserType() != null) {
                    key.setUserType(managerDTO.getUserType());
                } else {
                    key.setUserType(WrUseUnitEnum.USER_TYPE_MANAGER);
                }
                allUpdatePerson.add(key);
            });
        }
        if (personDTOList != null && personDTOList.size() != 0) {
            personDTOList.forEach(personDTO -> {
                PersonTransKey key = new PersonTransKey();
                key.setUserId(personDTO.getUserId());
                if (personDTO.getUserType() != null) {
                    key.setUserType(personDTO.getUserType());
                } else {
                    key.setUserType(WrUseUnitEnum.USER_TYPE_RELATED_PERSON);
                }
                allUpdatePerson.add(key);
            });
        }


        if (allUpdatePerson.size() != 0) {
            //属于该单位的人员列表
            //过滤创建人
            String unitId = po.getId();
            List<WrUseUnitPerson> personOldList = personService.lambdaQuery()
                    .eq(WrUseUnitPerson::getUnitId, unitId)
                    .ne(WrUseUnitPerson::getUserType, WrUseUnitEnum.USER_TYPE_CREATOR)
                    .list();
            //ID TYPE做伪双主键
            List<PersonTransKey> personTransOldList = personOldList.stream().map(person -> new PersonTransKey(person.getUserId(), person.getUserType())).collect(Collectors.toList());
            Map<PersonTransKey, WrUseUnitPerson> personTransOldMap = personOldList.stream().collect(Collectors.toMap(person -> new PersonTransKey(person.getUserId(), person.getUserType()), person -> person));

            //过滤出要删除的人员
            //用伪双主键比较
            List<WrUseUnitPerson> personDelList = personTransOldList.stream().filter(personTrans -> !allUpdatePerson.contains(personTrans)).map(personTransOldMap::get).collect(Collectors.toList());
            //获取要删除人员的主键ID
            //删除对应人员
            List<String> personPKDelList = personDelList.stream().map(WrUseUnitPerson::getId).collect(Collectors.toList());
            personService.removeByIds(personPKDelList);

            //过滤出要新增的人员
            List<PersonTransKey> personTransAddList = allUpdatePerson.stream().filter(personTrans -> !personTransOldList.contains(personTrans)).collect(Collectors.toList());

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

        return getById(po.getId());
    }

    @Override
    public void deleteWaterUseUnitById(String id) {
        //如果是一级单位，先删除缓存树，再删除数据库
        //如果不是，先删除数据库记录，再更新缓存树
        WrUseUnitManager po = baseMapper.selectById(id);
        String pid = po.getPid();

        if (pid.equals(TreeEnum.WR_USE_UNIT_ROOT_PID)) {
            cacheService.delWaterUseUnitTree(id);
            //递归删除用水单位节点及所有子节点，包含人员关系
            transactionTemplate.executeWithoutResult(transactionStatus -> deleteOneWaterUseUnitById(id));
        } else {
            String rootId = getRootId(po);
            //递归删除用水单位节点及所有子节点，包含人员关系
            transactionTemplate.executeWithoutResult(transactionStatus -> deleteOneWaterUseUnitById(id));
            initModelCache.updateWaterUseUnitTree(rootId);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteOneWaterUseUnitById(String id) {
        //判断是否存在子用水单位
        List<WrUseUnitManager> poList = lambdaQuery()
                .eq(WrUseUnitManager::getPid, id)
                .list();
        if (poList != null && poList.size() != 0) {
            poList.forEach(sonPo -> deleteOneWaterUseUnitById(sonPo.getId()));
        }

        //先删除属于该用水单位的人员
        personService.lambdaUpdate().eq(WrUseUnitPerson::getUnitId, id).remove();
        //再删除该用水单位
        baseMapper.deleteById(id);
    }

    @Override
    public List<WrUseUnitManagerVO> getWaterUseUnitList(List<String> unitIds) {
        //查一批用水单位信息
        List<WrUseUnitManager> unitList = lambdaQuery()
                .in(unitIds != null && unitIds.size() != 0, WrUseUnitManager::getId, unitIds)
                .list();
        //无用水单位 直接返回
        if (unitList == null || unitList.size() == 0) {
            return new ArrayList<>();
        }
        List<WrUseUnitManagerVO> voList = convert2VOList(unitList);

        //查这些用水单位下所有人员
        List<String> idList = unitList.stream().map(WrUseUnitManager::getId).collect(Collectors.toList());
        List<WrUseUnitPerson> personList = personService.lambdaQuery()
                .in(WrUseUnitPerson::getUnitId, idList)
                .list();
        //整合用水单位的人员
        Map<String, WrUseUnitManagerVO> voMap = voList.stream().collect(Collectors.toMap(WrUseUnitManagerVO::getId, vo -> vo));

        personList.forEach(person -> {
            if (voMap.containsKey(person.getUnitId())) {
                WrUseUnitManagerVO vo = voMap.get(person.getUnitId());
                PersonTransDTO personTrans = new PersonTransDTO();
                personTrans.setUserId(person.getUserId());
                personTrans.setUserType(person.getUserType());
                //缓存获取用户名
                String userName = cacheService.getUserName(person.getUserId());
                personTrans.setUserName(userName);
                vo.getPersonList().add(personTrans);
            }
        });
        return new ArrayList<>(voMap.values());

    }

    @Override
    public List<WrUseUnitManagerVO> getAllWaterUseUnitList() {
        return getWaterUseUnitList(null);
    }

    @Override
    public DataTableVO getWaterUseUnitPage(Integer pageIndex, Integer pageSize, String pid, Integer state) {
        IPage<WrUseUnitManager> page = lambdaQuery()
                .eq(StringUtils.isNotEmpty(pid), WrUseUnitManager::getPid, pid)
                .eq(state != null, WrUseUnitManager::getState, state)
                .orderByAsc(WrUseUnitManager::getCode)
                .page(new Page<>(pageIndex, pageSize));
        List<WrUseUnitManager> unitList = page.getRecords();
        //无数据直接返回
        if (unitList == null || unitList.size() == 0) {
            return new DataTableVO();
        }
        //查询上级用水单位名称
        List<String> pIds = unitList.stream().map(WrUseUnitManager::getPid).distinct().collect(Collectors.toList());
        List<WrUseUnitManager> parents = new ArrayList<>();
        if (pIds.size() != 0) {
            parents = baseMapper.selectBatchIds(pIds);
        }
        Map<String, String> parentMap = parents.stream().collect(Collectors.toMap(WrUseUnitManager::getId, WrUseUnitManager::getUnitName));

        // 查这些用水单位下所有人员
        List<String> idList = unitList.stream().map(WrUseUnitManager::getId).collect(Collectors.toList());
        List<WrUseUnitPerson> creatorList = personService.lambdaQuery().in(WrUseUnitPerson::getUnitId, idList).eq(WrUseUnitPerson::getUserType, WrUseUnitEnum.USER_TYPE_CREATOR).list();
        List<WrUseUnitPerson> managerList = personService.lambdaQuery().in(WrUseUnitPerson::getUnitId, idList).eq(WrUseUnitPerson::getUserType, WrUseUnitEnum.USER_TYPE_MANAGER).list();
        List<WrUseUnitPerson> personList = personService.lambdaQuery().in(WrUseUnitPerson::getUnitId, idList).eq(WrUseUnitPerson::getUserType, WrUseUnitEnum.USER_TYPE_RELATED_PERSON).list();
        //整合用水单位的人员
        List<WrUseUnitManagerVO> voList = convert2VOList(unitList);
        voList.forEach(vo -> {
            if (parentMap.containsKey(vo.getPid())) {
                vo.setPunitName(parentMap.get(vo.getPid()));
            }
        });
        Map<String, WrUseUnitManagerVO> voMap = voList.stream().collect(Collectors.toMap(WrUseUnitManagerVO::getId, vo -> vo));

        creatorList.forEach(person ->
        {
            if (voMap.containsKey(person.getUnitId())) {
                WrUseUnitManagerVO vo = voMap.get(person.getUnitId());
                PersonTransDTO personTrans = new PersonTransDTO();
                personTrans.setUserId(person.getUserId());
                personTrans.setUserType(person.getUserType());
                String userName = cacheService.getUserName(person.getUserId());
                personTrans.setUserName(userName);
                vo.getCreatorList().add(personTrans);
            }
        });
        managerList.forEach(person ->
        {
            if (voMap.containsKey(person.getUnitId())) {
                WrUseUnitManagerVO vo = voMap.get(person.getUnitId());
                PersonTransDTO personTrans = new PersonTransDTO();
                personTrans.setUserId(person.getUserId());
                personTrans.setUserType(person.getUserType());
                String userName = cacheService.getUserName(person.getUserId());
                personTrans.setUserName(userName);
                vo.getManagerList().add(personTrans);
            }
        });
        personList.forEach(person ->
        {
            if (voMap.containsKey(person.getUnitId())) {
                WrUseUnitManagerVO vo = voMap.get(person.getUnitId());
                PersonTransDTO personTrans = new PersonTransDTO();
                personTrans.setUserId(person.getUserId());
                personTrans.setUserType(person.getUserType());
                String userName = cacheService.getUserName(person.getUserId());
                personTrans.setUserName(userName);
                vo.getPersonList().add(personTrans);
            }
        });
        voList = new ArrayList<>(voMap.values());

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
    public List<WrUseUnitNode> getAllTreeFromCache() {
        List<WrUseUnitManager> poList = lambdaQuery()
                .eq(WrUseUnitManager::getPid, TreeEnum.WR_USE_UNIT_ROOT_PID)
                .list();
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
        List<WrUseUnitManager> poList = lambdaQuery()
                .eq(WrUseUnitManager::getPid, pid)
                .list();
        return poList.stream().map(WrUseUnitManager::getId).collect(Collectors.toList());
    }


    @Override
    public WrUseUnitNode getTreeFromCacheById(String id) {
        return cacheService.getWaterUseUnitTree(id);
    }

    @Override
    public Boolean checkUniqueCode(String code) {
        Integer count = lambdaQuery().eq(StringUtils.isNotEmpty(code), WrUseUnitManager::getCode, code).count();
        return count == 0;
    }

    protected WrUseUnitManagerVO convert2VO(WrUseUnitManager po) {
        WrUseUnitManagerVO vo = new WrUseUnitManagerVO();
        BeanUtils.copyProperties(po, vo);
        if (po.getHousesTime() != null) {
            vo.setHousesTime(DateUtils.convertDateToLong(po.getHousesTime()));
        }
        return vo;
    }

    protected List<WrUseUnitManagerVO> convert2VOList(List<WrUseUnitManager> poList) {
        List<WrUseUnitManagerVO> voList = new ArrayList<>();
        if (poList == null || poList.size() == 0) {
            return voList;
        }

        poList.forEach(po -> {
            WrUseUnitManagerVO vo = convert2VO(po);
            voList.add(vo);
        });
        return voList;
    }


}
