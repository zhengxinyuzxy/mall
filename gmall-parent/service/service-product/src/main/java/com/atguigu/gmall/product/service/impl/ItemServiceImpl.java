package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 商品详情页的内部实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 查询关于sku_info表的所有信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    /**
     * 根据redisorDb查询关于sku_info表的所有信息， redis单点情况
     * 使用UUID作为锁的值保证唯一
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoFromRedisOrDb(Long skuId) {
        // 参数校验
        if (skuId == null) {
            throw new RuntimeException("参数异常!");
        }
        // 锁的键
        String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
        // UUID生成锁的键的值
        UUID uuid = UUID.randomUUID();
        // 加锁
        if (redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, 60, TimeUnit.SECONDS)) {
            String key = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
            // 查询redis
            SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(key);
            // 查询redis中是否有数据，有直接返回
            if (skuInfo != null) {
                return skuInfo;
            }
            // redis中没有，查数据库
            skuInfo = getSkuInfo(skuId);
            // 查询数据库，将数据库结果保存入redis
            // 判断skuInfo是否有值
            if (skuInfo == null || skuInfo.getId() == null) {
                // 数据库没有值，依然写入redis，防止缓存穿透
                skuInfo = new SkuInfo();
                // 防止有人写入缓存无法写入，设置一个过期时间
                redisTemplate.opsForValue().set(key, skuInfo, 300, TimeUnit.SECONDS);
            } else {
                // 数据库有值，写入redis
                redisTemplate.opsForValue().set(key, skuInfo);
            }
            // 释放锁
            DefaultRedisScript<Long> script = new DefaultRedisScript();
            script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            script.setResultType(Long.class);
            redisTemplate.execute(script, Arrays.asList(lockKey), uuid);
            // 返回结果
            return skuInfo;
        } else {
            try {
                // 递归调用频次降低
                Thread.sleep(10);
                return getSkuInfoFromRedisOrDb(skuId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 使用redisson从redis和数据库中查询skuInfo的信息，redis集群情况
     *
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfoFromRedisson(Long skuId) {
        // 参数校验
        if (skuId == null) {
            throw new RuntimeException("参数异常!");
        }
        // 查缓存
        String key = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        // 查询redis
        SkuInfo skuInfo = (SkuInfo) redisTemplate.opsForValue().get(key);
        // 查询redis中是否有数据，有直接返回
        if (skuInfo != null) {
            return skuInfo;
        }

        String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
        // 获取锁
        RLock lock = redissonClient.getLock(lockKey);
        // 加锁
        try {
            if (lock.tryLock(20, 20, TimeUnit.SECONDS)) {
                // redis中没有，查数据库
                skuInfo = getSkuInfo(skuId);
                // 查询数据库，将数据库结果保存入redis
                // 判断skuInfo是否有值
                if (skuInfo == null || skuInfo.getId() == null) {
                    // 数据库没有值，依然写入redis，防止缓存穿透
                    skuInfo = new SkuInfo();
                    // 防止有人写入缓存无法写入，设置一个过期时间
                    redisTemplate.opsForValue().set(key, skuInfo, 300, TimeUnit.SECONDS);
                } else {
                    // 数据库有值，写入redis
                    redisTemplate.opsForValue().set(key, skuInfo);
                }
                // 返回结果
                return skuInfo;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // 释放锁
            lock.unlock();
        }


    }

    /**
     * 根据三级分类查询一级二级三级分类信息
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getBaseCategoryView(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 查询图片列表
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        return skuImageMapper.selectList(
                new LambdaQueryWrapper<SkuImage>()
                        .eq(SkuImage::getSkuId, skuId));
    }

    /**
     * 根据skuId查询sku_info表的price价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuInfoPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    /**
     * 根据spuId, skuId查询销售属性和属性值, 标识唯一的选中商品
     *
     * @param spuId
     * @param skuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrBySpuIdBySkuId(Long spuId, Long skuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuIdBySkuId(spuId, skuId);
    }

    /**
     * 根据spu的id查询sku的销售属性键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuSaleAttrValueKeys(Long spuId) {

        // 声明新的map集合用来交换list集合中每个map的键和值
        Map<String, Object> map = new HashMap<>();

        List<Map> maps = skuSaleAttrValueMapper.selectSkuSaleAttrValueKeys(spuId);

        // 遍历maps, 拿到唯一的销售属性map

        for (Map value : maps) {
            Object skuId = value.get("sku_id");
            String skuSaleAttrValuesId = value.get("skuSaleAttrValuesId").toString();
            map.put(skuSaleAttrValuesId, skuId);
        }
        return map;
    }

    /**
     * 根据skuId查询品牌信息
     *
     * @param skuId
     * @return
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        return baseTrademarkMapper.selectById(id);
    }

    /**
     * 根据skuId查询销售属性信息
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
    }

    /**
     * 扣减库存
     *
     * @param map
     */
    @Override
    public Boolean decountStock(Map<String, String> map) {
        //参数校验
        if (map == null || map.size() == 0) {
            return false;
        }
        //循环遍历map,循环扣减库存
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //商品id
            String key = entry.getKey();
            //扣减的数量
            String value = entry.getValue();
            skuInfoMapper.decountStock(Long.parseLong(key), Integer.parseInt(value));
//            //查询商品的信息
//            SkuInfo skuInfo = skuInfoMapper.selectById(key);
//            if(skuInfo == null || skuInfo.getId() == null){
//                throw new RuntimeException(key + "商品的库存不足!商品已经不存在了!");
//            }
//            //扣减库存
//            Integer stock = skuInfo.getStock() - Integer.parseInt(value);
//            if(stock < 0){
//                throw new RuntimeException("商品的库存不足!");
//            }
//            skuInfo.setStock(stock);
//            //更新数据
//            skuInfoMapper.updateById(skuInfo);
        }
        return true;
    }

    /**
     * 回滚库存
     *
     * @param map
     */
    @Override
    public Boolean rollbackStock(Map<String, String> map) {
        //参数校验
        if (map == null || map.size() == 0) {
            return false;
        }
        //循环遍历map,循环扣减库存
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //商品id
            String key = entry.getKey();
            //扣减的数量
            String value = entry.getValue();
            //回滚库存
            skuInfoMapper.rollbackStock(Long.parseLong(key), Integer.parseInt(value));
        }
        return true;
    }
}
