package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.shoppingmall.product.entity.AttrEntity;
import com.anzaiyun.shoppingmall.product.service.AttrAttrgroupRelationService;
import com.anzaiyun.shoppingmall.product.service.AttrService;
import com.anzaiyun.shoppingmall.product.vo.AttrGroupWithAttrsVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuItemVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SpuAttrGroupVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SpuBaseAttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.AttrGroupDao;
import com.anzaiyun.shoppingmall.product.entity.AttrGroupEntity;
import com.anzaiyun.shoppingmall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService attrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if (!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        if (catelogId==0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );

            return new PageUtils(page);
        }else{
            wrapper.eq("catelog_id",catelogId);

            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );

            return new PageUtils(page);
        }
    }

    /**
     * 返回分组关联的规格参数信息
     * @param params
     * @param groupId
     * @return
     */
    @Override
    public List<AttrEntity> queryAttrByGroupId(Map<String, Object> params, Long groupId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id",groupId);

        List<AttrEntity> data = attrService.list(queryWrapper);

        return data;
    }

    /**
     * 根据分类id查询当前分类下的所有规格参数信息，并按照所属的分组返回
     * @param catId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catId) {
        List<AttrGroupEntity> attrGroups = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));

        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVoList = attrGroups.stream().map(
                attrGroup -> {
                    AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
                    BeanUtils.copyProperties(attrGroup,attrsVo);
                    List<AttrEntity> attrList = attrService.list(new QueryWrapper<AttrEntity>().eq("attr_group_id", attrGroup.getAttrGroupId()));
                    attrsVo.setAttrs(attrList);
                    return attrsVo;
                }
        ).collect(Collectors.toList());
        return attrGroupWithAttrsVoList;
    }

    @Override
    public List<SpuAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId) {

        //根据spuid获取attr,并根据attr获取对应的分组信息
        List<AttrGroupEntity> attrGroups = this.baseMapper.getAttrGroupBySpuId(spuId);

        //根据分组获取对应的attr信息
        List<SpuAttrGroupVo> collect = attrGroups.stream().map(group -> {
            SpuAttrGroupVo attrGroupVo = new SpuAttrGroupVo();
            attrGroupVo.setGroupName(group.getAttrGroupName());
            attrGroupVo.setGroupId(group.getAttrGroupId());

            List<SpuBaseAttrVo> baseAttrVos  = attrgroupRelationService.getDetailAttrByGroupId(spuId, group.getAttrGroupId());

            attrGroupVo.setAttrs(baseAttrVos);

            return attrGroupVo;
        }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public List<SpuAttrGroupVo> getAttrGroupWithAttrsBySpuIdSimple(Long spuId) {
        List<SpuAttrGroupVo> spuAttrGroupVos = this.baseMapper.getAttrGroupWithAttrsBySpuId(spuId);

        return spuAttrGroupVos;
    }

}