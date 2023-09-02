package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestDLockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestDLockServiceImpl implements TestDLockService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 不使用数据库加锁是因为容易造成死锁

    /**
     * 例子1：单点不并发
     * 1. 单点不并发向redis中设置键值, 数据安全, 没有并发问题
     * 2. 单点并发向redis中设置键值, 数据不安全, 有并发问题
     */
//    @Override
//    public void setValue() {
//        // 获取redis中key为abc的值
//        Integer abc = (Integer)redisTemplate.opsForValue().get("abc");
//        // 判断abc的值是否为空, 不为空+1
//        if (abc == null) {
//            return;
//        }
//        // 加1操作
//        ++abc;
//        // 执行+1操作后存入redis
//        redisTemplate.opsForValue().set("abc", abc);
//    }

    /**
     * 例子2：单点并发使用synchronized(本地锁)锁
     * 1. 单点并发向redis中设置键值, 使用synchronized(本地锁)锁, 并发情况下没有数据不安全问题
     */
//    @Override
//    public synchronized void setValue() {
//        // 获取redis中key为abc的值
//        Integer abc = (Integer)redisTemplate.opsForValue().get("abc");
//        // 判断abc的值是否为空, 不为空+1
//        if (abc == null) {
//            return;
//        }
//        // 加1操作
//        ++abc;
//        // 执行+1操作后存入redis
//        redisTemplate.opsForValue().set("abc", abc);
//    }

//    /**
//     * 例子3：多点集群synchronized(本地锁)锁
//     * 1. 多点集群并发向redis中设置键值, 使用synchronized(本地锁)锁, 数据不安全
//     * 2. 结论: 本地锁无法解决分布式情况下对redis中的键值紊乱问题, 原因多个微服务多把锁, 锁与锁之间都是互不影响
//     */
//    @Override
//    public synchronized void setValue() {
//        // 获取redis中key为abc的值
//        Integer abc = (Integer)redisTemplate.opsForValue().get("abc");
//        // 判断abc的值是否为空, 不为空+1
//        if (abc == null) {
//            return;
//        }
//        // 加1操作
//        ++abc;
//        // 执行+1操作后存入redis
//        redisTemplate.opsForValue().set("abc", abc);
//    }

    /**
     * 例子4：多点并发使用redis加锁
     * 1. 多点并发使用redis实现分布式锁, 数据是安全的
     * 2. setIfAbsent相当于setNX, setNX理解为当Key存在失败, key不存在时成功
     * 3. 一定要释放锁
     */
//    @Override
//    public void setValue() {
//        // 尝试使用redis加锁
//        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("lockKey", "123");
//        // 加锁成功进行++操作, 否则一直尝试加锁
//        if (aBoolean) {
//            // 获取redis中key为abc的值
//            Integer abc = (Integer)redisTemplate.opsForValue().get("abc");
//            // 判断abc的值是否为空, 不为空+1
//            if (abc == null) {
//                return;
//            }
//            // 加1操作
//            ++abc;
//            // 执行+1操作后存入redis
//            redisTemplate.opsForValue().set("abc", abc);
//            // 只有一个拿到锁, 其他一直加锁会一直失败, 所以一定要释放锁
//            redisTemplate.delete("lockKey");
//        } else {
//            try {
//                Thread.sleep(1000);
//                // 失败则递归调用一直尝试加锁
//                setValue();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 例子5：多点并发使用redis加锁, 中途出现问题没有到释放锁, 死锁问题, 加过期时间
     * 1. 多点并发使用redis实现分布式锁, 数据是安全的
     * 2. setIfAbsent相当于setNX, setNX理解为当Key存在失败, key不存在时成功
     * 3. 一定要释放锁
     * 4. 制造异常, 没有释放锁, 造成死锁, 需要加过期时间
     */
//    @Override
//    public void setValue() {
//        // 尝试使用redis加锁
//        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("lockKey", "123", 1000, TimeUnit.SECONDS);
//        // 加锁成功进行++操作, 否则一直尝试加锁
//        if (aBoolean) {
//            // 获取redis中key为abc的值
//            Integer abc = (Integer) redisTemplate.opsForValue().get("abc");
//            // 判断abc的值是否为空, 不为空+1
//            if (abc == null) {
//                return;
//            }
//            // 加1操作
//            ++abc;
//            // 执行+1操作后存入redis
//            redisTemplate.opsForValue().set("abc", abc);
//            // 制造异常, 此时出现问题, 没有释放锁, 造成死锁
//            int i = 10 / 0;
//            // 只有一个拿到锁, 其他一直加锁会一直失败, 所以一定要释放锁
//            redisTemplate.delete("lockKey");
//        } else {
//            try {
//                Thread.sleep(1000);
//                // 失败则递归调用一直尝试加锁
//                setValue();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 例子6：多点并发使用redis加锁, 释放锁时误删锁, 使用UUID表示值进行对比, 确定是不是自己的锁, 但存在时间差, 依然存在锁误删
     * 1. 多点并发使用redis实现分布式锁, 数据是安全的
     * 2. setIfAbsent相当于setNX, setNX理解为当Key存在失败, key不存在时成功
     * 3. 一定要释放锁
     * 4. 制造异常, 没有释放锁, 造成死锁, 需要加过期时间
     */
//    @Override
//    public void setValue() {
//        // 生成UUID
//        UUID uuid = UUID.randomUUID();
//
//        // 尝试使用redis加锁, uuid替代键的值
//        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("lockKey", uuid, 1000, TimeUnit.SECONDS);
//        // 加锁成功进行++操作, 否则一直尝试加锁
//        if (aBoolean) {
//            // 获取redis中key为abc的值
//            Integer abc = (Integer) redisTemplate.opsForValue().get("abc");
//            // 判断abc的值是否为空, 不为空+1
//            if (abc == null) {
//                return;
//            }
//            // 加1操作
//            ++abc;
//            // 执行+1操作后存入redis
//            redisTemplate.opsForValue().set("abc", abc);
//
//            // 释放锁：
//            // 判断是否是自己的锁, 是直接删除释放锁, 不是结束
//            // 但是依然存在锁误删, 因为存在时间差
//            String lockKey = (String) redisTemplate.opsForValue().get("lockKey");
//            // 判断uuid是否一致, 一致表示当前锁是自己的
//            if (uuid.equals(lockKey)) {
//                // 只有一个拿到锁, 其他一直加锁会一直失败, 所以一定要释放锁
//                redisTemplate.delete("lockKey");
//            } else {
//                // 锁不是自己的, 直接返回结果
//                return;
//            }
//        } else {
//            try {
//                Thread.sleep(1000);
//                // 失败则递归调用一直尝试加锁
//                setValue();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 例子7：多点并发使用单点redis加锁, 释放锁时误删锁, 使用luo表达式, luo脚步一步释放锁, 解决时间差
     * 1. 多点并发使用redis实现分布式锁, 数据是安全的
     * 2. setIfAbsent相当于setNX, setNX理解为当Key存在失败, key不存在时成功
     * 3. 一定要释放锁
     * 4. 制造异常, 没有释放锁, 造成死锁, 需要加过期时间
     */
    @Override
    public void setValue() {
        // 生成UUID
        UUID uuid = UUID.randomUUID();

        // 尝试使用redis加锁, uuid替代键的值
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("lockKey", uuid, 1000, TimeUnit.SECONDS);
        // 加锁成功进行++操作, 否则一直尝试加锁
        if (aBoolean) {
            // 获取redis中key为abc的值
            Integer abc = (Integer) redisTemplate.opsForValue().get("abc");
            // 判断abc的值是否为空, 不为空+1
            if (abc == null) {
                return;
            }
            // 加1操作
            ++abc;
            // 执行+1操作后存入redis
            redisTemplate.opsForValue().set("abc", abc);

            // 释放锁：
            // 使用Luo脚步解决存在时间差问题的锁误删
            // 声明luo脚步对象
            DefaultRedisScript<Long> script = new DefaultRedisScript();
            // 设置执行脚本的内容, 判断, 删除, 返回Long类型结果
            script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            // 声明返回结果类型
            script.setResultType(Long.class);
            // 执行Luo脚步
            redisTemplate.execute(script, Arrays.asList("lockKey"), uuid);
        } else {
            try {
                Thread.sleep(1000);
                // 失败则递归调用一直尝试加锁
                setValue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 例子8：多点并发使用redisson多点redis加锁(现在的redis是单点)
     * setValue
     */
    @Override
    public void setValueRedisson() {
        // 获取锁
        // 参数只有锁的键，值是线程ID，threadId
        RLock lock = redissonClient.getLock("lockKey");
        try {
            // 加锁
            if (lock.tryLock(20, 20, TimeUnit.SECONDS)) {
                // 尝试使用redis加锁
                // 加锁成功进行++操作, 否则一直尝试加锁
                // 获取redis中key为abc的值
                Integer abc = (Integer) redisTemplate.opsForValue().get("abc");
                // 判断abc的值是否为空, 不为空+1
                if (abc == null) {
                    return;
                }
                // 加1操作
                ++abc;
                // 执行+1操作后存入redis
                redisTemplate.opsForValue().set("abc", abc);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放锁
            lock.unlock();
        }


    }

}
