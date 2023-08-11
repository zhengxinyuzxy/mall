package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 增强方法，@annotation监听的作用域是com.atguigu.gmall.common.cache.GmallCache，任何加了GmallCache的方法都会被监听
     * point是方法执行的进度
     *
     * @param point
     * @return
     */
    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {

        /*
        1.  获取参数列表
        2.  获取方法上的注解
        3.  获取前缀
        4.  获取目标方法的返回值
         */
        Object result = null;
        try {
            // 获取得到增强的方法的参数
            Object[] args = point.getArgs();
            // 获取方法的签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            // signature可以获取到整个方法对象
            // 使用签名的目的是获取被增强的方法的对象，拿到该被增强方法的注解，从而拿到前缀
            GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
            // 前缀
            String prefix = gmallCache.prefix();
            // 从缓存中获取数据
            // prefix:[args]
            String key = prefix + Arrays.asList(args).toString();

            // 获取缓存数据，此时还没有执行被增强的方法，只是查redis缓存
            result = cacheHit(signature, key);
            if (result != null) {
                // 缓存有数据
                return result;
            }
            // 初始化分布式锁
            // 设置锁的键值对方便和redis缓存的键值对区分,prefix:[lock]
            String lockKey = prefix + RedisConst.SKULOCK_SUFFIX;
            RLock lock = redissonClient.getLock(lockKey);
            // 加锁
            boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if (flag) {
                try {
                    try {
                        result = point.proceed(point.getArgs());
                        // 防止缓存穿透
                        if (null == result) {
                            // 并把结果放入缓存
                            Object o = new Object();
                            // 当redis中的数据null时，防止击穿，设置空值的过期时间
                            this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(o), 300, TimeUnit.SECONDS);
                            return o;
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    // 并把结果放入缓存
                    this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(result));
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 释放锁
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //boolean flag = lock.tryLock(10L, 10L, TimeUnit.SECONDS);
        return result;
    }

    /**
     * @param signature
     * @param key：      prefix:[args]
     * @return
     */
    // 获取缓存数据
    private Object cacheHit(MethodSignature signature, String key) {
        // 1. 查询缓存
        String cache = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(cache)) {
            // 有，则反序列化，直接返回
            // signature拿到整个方法对象，获得被增强方法的返回类型
            Class returnType = signature.getReturnType(); // 获取方法返回类型
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            // 反序列化拿到和被增强方法返回值类型相同的返回对象
            return JSONObject.parseObject(cache, returnType);
        }
        return null;
    }

}
