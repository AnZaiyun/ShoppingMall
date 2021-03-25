package com.anzaiyun.shoppingmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.anzaiyun.shoppingmall.product.service.CategoryBrandRelationService;
import com.anzaiyun.shoppingmall.product.vo.Catalog2JsonVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    RedissonClient redisson;

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

    @Cacheable(cacheNames = "{category}",key = "#root.method.toString()")
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
            //本地式锁
            //catalogJsonFromDb = getCatalogJsonFromDbAddLocalLock();
            //分布式锁，因为上锁解锁，需要进行网络通信，所以分布式锁也会影响部分性能
            catalogJsonFromDb = getCatalogJsonFromDbAddRedisLock();
        }else{
            //将json字符串转换成指定对象(反序列化)
            catalogJsonFromDb = JSONObject.parseObject(catalogJson,new TypeReference<Map<String, List<Catalog2JsonVo>>>(){});
        }

        return catalogJsonFromDb;
    }

    /**
     * 加上redisson的分布式锁
     * @return
     */
    public Map<String, List<Catalog2JsonVo>> getCatalogJsonFromDbAddRedissonLock() {
        RLock lock = redisson.getLock("catalogJson-lock");
        Map<String, List<Catalog2JsonVo>> catalogJsonFromDb = new HashMap<>();
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");

        if (StringUtils.isEmpty(catalogJson)){
            lock.lock();
            try {
                catalogJsonFromDb = getCatalogJsonFromDb();
                stringRedisTemplate.opsForValue().set("catalogJson", JSONObject.toJSONString(catalogJsonFromDb));
            }finally {
                lock.unlock();
            }
        }else {
            catalogJsonFromDb = JSONObject.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2JsonVo>>>() {
            }); 
        }

        return catalogJsonFromDb;
    }

    /**
     * 加上redis的分布式锁
     * @return
     */
    public Map<String, List<Catalog2JsonVo>> getCatalogJsonFromDbAddRedisLock() {

        /**
         * 对于每一个服务用相同的key进行上锁，分布式锁需要考虑以下情况
         * 1)此处对redis缓存存值（上锁），如果不设置过期时间，此处上锁后，如果在服务的处理过程中出现异常，
         * 锁将无法删除，其他的服务将一直等待锁，造成服务异常，因此需要设置过期时间
         * 2）设置过期时间后，服务执行完会删除锁，此时会出现这种情况，服务的执行时间过长，超过了锁的过期时间，这时当前服务A的锁会自动删除
         * 其他等待服务B则会获取到锁，再进行上锁过程，而这时当前服务A执行完毕进行解锁操作就会将其他服务B的锁删除，服务C获取到锁进行处理，
         * 这时就可能出现异常，为了解决中情况，每个服务锁的value就应该是不一样的值，删除锁时需要判断锁属不属于当前服务
         * 3)经过以上两种场景的考虑，删除锁操作很容易的想到删除前先获取锁，查看value是否和上锁时一致，如果一致再删除
         * 但是这样会引申出以下这种问题：在高并发的场景下，获取锁时获取到了value，此时value与上锁时一致，但是因为网络延迟，服务A在接收到
         * 这个value的过程中，服务A的锁恰好自动到期了，此时服务B获取到锁，并进行了加锁操作。服务A获取到过期的value后，判断通过，认为当前
         * 的锁属于自己进行了删锁操作，此时就会将服务B的锁误删除。
         * 为了解决这种情况，就需要使用lua脚本的方法，将判断与删除的逻辑一并发给redis，由redis来进行判断删除操作
         * */
        Map<String, List<Catalog2JsonVo>> catalogJsonFromDb = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        //设置上锁与过期时间应该是原子操作
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            //二次校验，如果缓存中已经有数据，则直接返回
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            try {
                if (!StringUtils.isEmpty(catalogJson)) {
                    //缓存不为空直接返回
                    catalogJsonFromDb = JSONObject.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2JsonVo>>>() {
                    });
                } else {
                    System.out.println("获取到分布式锁，查询数据库中。。。");
                    catalogJsonFromDb = getCatalogJsonFromDb();
                    stringRedisTemplate.opsForValue().set("catalogJson", JSONObject.toJSONString(catalogJsonFromDb));
                }
            }finally {
                //删除锁，判断与删除应该是原子操作
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                stringRedisTemplate.execute(new DefaultRedisScript<Integer>(script, Integer.class), Arrays.asList("lock"), uuid);

            }


        }else {
            //自旋调用
            getCatalogJsonFromDbAddRedisLock();
        }

        return catalogJsonFromDb;
    }

    /**
     * 加上本地锁
     * @return
     */
    public Map<String, List<Catalog2JsonVo>> getCatalogJsonFromDbAddLocalLock() {

        //加锁，锁住所有线程
        //this锁是本地锁，只能锁住本地的服务，分布式场景需要分布式锁
        synchronized (this) {
            //二次校验，如果缓存中已经有数据，则直接返回
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if(!StringUtils.isEmpty(catalogJson)){
                //缓存不为空直接返回
                return JSONObject.parseObject(catalogJson,new TypeReference<Map<String, List<Catalog2JsonVo>>>(){});
            }else {
                System.out.println("获取到本地锁，查询数据库中。。。");
                //查库，并将数据保存到redis中
                Map<String, List<Catalog2JsonVo>> catalogJsonFromDb = getCatalogJsonFromDb();
                stringRedisTemplate.opsForValue().set("catalogJson", JSONObject.toJSONString(catalogJsonFromDb));
                return catalogJsonFromDb;
            }

        }


        //将查到的数据放入缓存,缓存存的应该是JSON字符串，其实这一段放在这里是不恰当的，加锁时会判断缓存中有没有数据，没有就查库
        //所以查库以及之后的逻辑都应该在库中进行，将数据处理好并将数据存入redis后，才可以解锁，不然还是会有多次查库
        //stringRedisTemplate.opsForValue().set("catalogJson", JSONObject.toJSONString(catalogJsonVoMap));

    }

    public Map<String, List<Catalog2JsonVo>> getCatalogJsonFromDb(){

        Map<String, List<Catalog2JsonVo>> catalogJsonVoMap = new HashMap<>();
        List<CategoryEntity> categoryLevel2 = new ArrayList<>();
        List<CategoryEntity> categoryLevel3 = new ArrayList<>();

        //获取2级分类
        categoryLevel2 = this.getCategoryByLevel(2L);
        //获取3级分类
        categoryLevel3 = this.getCategoryByLevel(3L);

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