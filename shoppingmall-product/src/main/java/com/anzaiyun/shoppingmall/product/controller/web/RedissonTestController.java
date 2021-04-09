package com.anzaiyun.shoppingmall.product.controller.web;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁RedissonClient
 */
@Controller
public class RedissonTestController {

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 普通锁测试
     * @return
     */
    @ResponseBody
    @RequestMapping("/hello")
    public String redissonLock(){

        RLock lock = redisson.getLock("myLock01");
        /**
         * 指定了过期时间后，锁超时后，lock不会再自动续期
         */
        lock.lock(5, TimeUnit.SECONDS);
        try {
            System.out.println("已加锁，开始处理业务。。。"+Thread.currentThread().getId());
            Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.out.println("释放锁。。。"+Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }


    /**
     * 读写锁测试-写入
     * 读+读：相当于无锁，所有的读锁都会加锁成功
     * @return
     */
    @ResponseBody
    @RequestMapping("/redissonWriteLock")
    public String redissonWriteLock (){

        String s = "";
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock lock = readWriteLock.writeLock();
        try {

            lock.lock();
            s= UUID.randomUUID().toString();
            Thread.sleep(10*1000);
            stringRedisTemplate.opsForValue().set("writeLockValue",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }


        return "redissonWriteLock:"+s;

    }

    /**
     * 读写锁测试-读取
     * @return
     */
    @ResponseBody
    @RequestMapping("/redissonReadLock")
    public String redissonReadLock (){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock lock = readWriteLock.readLock();

        String s="";

        try {
            lock.lock();
            s = stringRedisTemplate.opsForValue().get("writeLockValue");
        }finally {
            lock.unlock();
        }

        return "redissonReadLock:"+s;

    }

    /**
     * 信号量测试-消耗
     * 信号量的应用场景之一就是限流
     * @return
     */
    @ResponseBody
    @RequestMapping("/park")
    public String park(){

        RSemaphore park = redisson.getSemaphore("park");

        try {
            park.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "park";
    }

    /**
     * 信号量测试-增加
     * @return
     */
    @ResponseBody
    @RequestMapping("/go")
    public String go(){
        RSemaphore park = redisson.getSemaphore("park");

        //假如park信号量的初始值是3，且当前没有占用的信号量未释放，如果一直调用release方法，park的值也会一直增加，超过3
        park.release();

        return "go";
    }

    /**
     * 测试闭锁
     * 关门案例，教室关门，需要等待所有的课程结束
     * @return
     */
    @ResponseBody
    @RequestMapping("/closeDoor")
    public String closeDoor(){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        try {
            door.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "door is clossing";
    }

    @ResponseBody
    @RequestMapping("/endClass/{id}")
    public String  endClass(@PathVariable("id") Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown();
        return String.format("Class %s has finished", id.toString());
    }
}
