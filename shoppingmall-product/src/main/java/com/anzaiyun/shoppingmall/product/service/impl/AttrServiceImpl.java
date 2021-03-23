package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.common.constant.ProductConstant;
import com.anzaiyun.shoppingmall.product.dao.AttrAttrgroupRelationDao;
import com.anzaiyun.shoppingmall.product.dao.AttrGroupDao;
import com.anzaiyun.shoppingmall.product.dao.CategoryDao;
import com.anzaiyun.shoppingmall.product.entity.AttrAttrgroupRelationEntity;
import com.anzaiyun.shoppingmall.product.entity.AttrGroupEntity;
import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;
import com.anzaiyun.shoppingmall.product.service.CategoryService;
import com.anzaiyun.shoppingmall.product.vo.AttrRespVo;
import com.anzaiyun.shoppingmall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.AttrDao;
import com.anzaiyun.shoppingmall.product.entity.AttrEntity;
import com.anzaiyun.shoppingmall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrgroupRelationDao attrgroupRelationDao;

    @Resource
    AttrGroupDao attrGroupDao;

    @Resource
    CategoryDao categoryDao;

    @Resource
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = {})
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //保存基本数据
        //这里保存数据后，会将保存入库后的数据查询出来，在存到attrEntity对象中
        this.save(attrEntity);
        //attr表中包含attr_group_id字段，但是当前attr实体类未包含这个字段，暂不清楚原因为何，因此在这里单独写一段更新groupid的逻辑
        this.baseMapper.updateAttrGroupIdByAttrId(attrEntity.getAttrId(),attr.getAttrGroupId());
        //保存关联关系，只有基本属性才需要保存分组信息
        if(attr.getAttrType() == 1) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrpage(Map<String, Object> params, Long catId, String attrType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("attr_type","base".equalsIgnoreCase(attrType)?1:0);
        if(catId != 0){
            wrapper.eq("catelog_id",catId);
        }

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((wrapper1)->{
                wrapper1.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        List<AttrEntity> records = page.getRecords();
        PageUtils pageUtils = new PageUtils(page);

        //以下语句出现了循环查库？？？
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            AttrAttrgroupRelationEntity attrId = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));

            if (attrId != null && "base".equalsIgnoreCase(attrType)) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                if (attrGroupEntity!= null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }


            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);

        //设置分组信息
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        //设置分类信息
        Long[] catelogPath = categoryService.findCatelogIds(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if(categoryEntity!=null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }

        return attrRespVo;
    }

    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.baseMapper.updateById(attrEntity);

        //attr表中包含attr_group_id字段，但是当前attr实体类未包含这个字段，暂不清楚原因为何，因此在这里单独写一段更新groupid的逻辑
        this.baseMapper.updateAttrGroupIdByAttrId(attrEntity.getAttrId(),attr.getAttrGroupId());

        //只有基本属性才需要保存分组信息,这里使用枚举类型的方式
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //先从attrgroup关系表中查找，是否存在数据，如果存在就更新，如果不存在就插入，其实也可以直接删除原有的数据
            attrgroupRelationDao.delete(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));

            AttrAttrgroupRelationEntity attrGroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrGroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrGroupRelationEntity.setAttrId(attr.getAttrId());
            attrgroupRelationDao.insert(attrGroupRelationEntity);
        }
    }

    @Override
    public List<Long> getSearchAttrIdById(List<Long> attrIdList) {
        return this.baseMapper.getSearchAttrIdById(attrIdList);
    }

}