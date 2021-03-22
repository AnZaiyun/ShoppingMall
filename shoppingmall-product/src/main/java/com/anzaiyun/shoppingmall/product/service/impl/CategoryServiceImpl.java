package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.shoppingmall.product.service.CategoryBrandRelationService;
import com.anzaiyun.shoppingmall.product.vo.Catalog2JsonVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.CategoryDao;
import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;
import com.anzaiyun.shoppingmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //组装成树形结构

        //找到所有的一级分类
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter(categories -> categories.getParentCid() ==0)
                .map((menu)->{
                    menu.setChildren(getChildrens(menu,categoryEntities));
                    return menu;
                }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());

        return level1Menus;
    }

    /**
     * 递归查找所有菜单的子菜单，二级分类，三级分类。。。
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){

        List<CategoryEntity> children = all.stream().filter((categoryEntity)->{
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildrens(categoryEntity,all));
            return categoryEntity;
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());

        return children;

    }

    /**
     * 带校验的删除
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 校验逻辑
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 增加新菜单
     * @param category
     */
    @Override
    public void addMenus(CategoryEntity category) {
        //TODO 校验当前要增加的菜单是否已经存在

        baseMapper.insert(category);

    }

    /**
     * 找到catelog的完整路径，格式:父路径。。。子路径
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogIds(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        Collections.reverse(findParentPath(catelogId,paths));
        this.getById(catelogId);
        return (Long[]) paths.toArray(new Long[paths.size()]);
    }

    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Override
    public List<CategoryEntity> getCategoryByLevel(Long catLevel) {

        List<CategoryEntity> categoryEntityList = this.list(new QueryWrapper<CategoryEntity>().eq("cat_level", catLevel));
        return categoryEntityList;
    }

    @Override
    public Map<String, List<Catalog2JsonVo>> getCatalogJson() {
        Map<String, List<Catalog2JsonVo>> catalogJsonVoMap = new HashMap<>();
        //获取2级分类
        List<CategoryEntity> categoryLevel2 = this.getCategoryByLevel(2L);
        //获取3级分类
        List<CategoryEntity> categoryLevel3 = this.getCategoryByLevel(3L);

        for (CategoryEntity catalog2:categoryLevel2) {
            Long level2Id = catalog2.getCatId();
            List<Catalog2JsonVo.Catalog3Vo> catalog3Vos = new ArrayList<>();
            for (CategoryEntity catalog3:categoryLevel3){
                if (catalog3.getParentCid().equals(level2Id)){
                    catalog3Vos.add(new Catalog2JsonVo.Catalog3Vo(level2Id,catalog3.getCatId(),catalog3.getName()));
                }
            }
            Catalog2JsonVo catalog2JsonVo = new Catalog2JsonVo(catalog2.getParentCid(), catalog3Vos, level2Id, catalog2.getName());
            if (catalogJsonVoMap.containsKey(catalog2.getParentCid())){
                List<Catalog2JsonVo> catalog2JsonVos = catalogJsonVoMap.get(catalog2.getParentCid());
                catalog2JsonVos.add(catalog2JsonVo);
                catalogJsonVoMap.put(catalog2.getParentCid().toString(),catalog2JsonVos);
            }else {
                List<Catalog2JsonVo> catalog2JsonVos = new ArrayList<>();
                catalog2JsonVos.add(catalog2JsonVo);
                catalogJsonVoMap.put(catalog2.getParentCid().toString(),catalog2JsonVos);
            }

        }
        return catalogJsonVoMap;
    }

    private  List<Long> findParentPath(Long catelogId,List<Long> paths){
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);

        if(byId.getParentCid()!=0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;

    }


}