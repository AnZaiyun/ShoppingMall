package com.anzaiyun.shoppingmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.anzaiyun.shoppingmall.product.service.CategoryBrandRelationService;
import com.anzaiyun.shoppingmall.product.vo.Catalog2JsonVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 开启缓存功能
     */
    @Autowired
    StringRedisTemplate stringRedisTemplate;

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
        return paths.toArray(new Long[paths.size()]);
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

    /**
     * 引入缓存功能
     * @return
     */
    @Override
    public Map<String, List<Catalog2JsonVo>> getCatalogJson() {
        Map<String, List<Catalog2JsonVo>> catalogJsonFromDb = new HashMap<>();
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");

        //TODO 可能会产生堆外内存溢出
        //原因：1）springboot2.0以后默认使用lettuce作为操作redis的客户端，他使用netty进行网络通信，netty在连接的过程中如果没有指定内存大小
        // 会直接使用当前程序jvm的内存大小，内存不足产生了异常导致报错
        //解决方案：1）调大内存只是延缓问题出现的时间，并没有根本解决问题，因此可以切换使用jedis

        /**
         * 1、空结果缓存，解决缓存穿透问题
         * 2、key设置加随机的过期时间，解决缓存雪崩问题
         * 3、添加锁，解决缓存击穿问题
         */
        if(StringUtils.isEmpty(catalogJson)){
            //缓存中没有数据，则重新查库
            catalogJsonFromDb = getCatalogJsonFromDb();
            //将查到的数据放入缓存,缓存存的应该是JSON字符串
            stringRedisTemplate.opsForValue().set("catalogJson", JSONObject.toJSONString(catalogJsonFromDb));
        }else{
            //将json字符串转换成指定对象(反序列化)
            catalogJsonFromDb = JSONObject.parseObject(catalogJson,new TypeReference<Map<String, List<Catalog2JsonVo>>>(){});
        }

        return catalogJsonFromDb;
    }

    public Map<String, List<Catalog2JsonVo>> getCatalogJsonFromDb() {


        Map<String, List<Catalog2JsonVo>> catalogJsonVoMap = new HashMap<>();
        List<CategoryEntity> categoryLevel2 = new ArrayList<>();
        List<CategoryEntity> categoryLevel3 = new ArrayList<>();

        //加锁，锁住所有线程
        //this锁是本地锁，只能锁住本地的服务，分布式场景需要分布式锁
        synchronized (this) {
            //二次校验，如果缓存中已经有数据，则直接返回
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if(!StringUtils.isEmpty(catalogJson)){
                //缓存不为空直接返回
                return JSONObject.parseObject(catalogJson,new TypeReference<Map<String, List<Catalog2JsonVo>>>(){});
            }
            //获取2级分类
            categoryLevel2 = this.getCategoryByLevel(2L);
            //获取3级分类
            categoryLevel3 = this.getCategoryByLevel(3L);
        }

        for (CategoryEntity catalog2:categoryLevel2) {
            Long level2Id = catalog2.getCatId();
            List<Catalog2JsonVo.Catalog3Vo> catalog3Vos = new ArrayList<>();
            for (CategoryEntity catalog3:categoryLevel3){
                if (catalog3.getParentCid().equals(level2Id)){
                    catalog3Vos.add(new Catalog2JsonVo.Catalog3Vo(level2Id,catalog3.getCatId(),catalog3.getName()));
                }
            }
            Catalog2JsonVo catalog2JsonVo = new Catalog2JsonVo(catalog2.getParentCid(), catalog3Vos, level2Id, catalog2.getName());

            if (catalogJsonVoMap.containsKey(catalog2.getParentCid().toString())){
                List<Catalog2JsonVo> catalog2JsonVos = catalogJsonVoMap.get(catalog2.getParentCid().toString());
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