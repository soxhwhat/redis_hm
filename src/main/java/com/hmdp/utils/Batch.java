package com.hmdp.utils;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/5/17 16:10
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */

import org.redisson.api.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * redisson批量执行
 *
 */
public class Batch{
    /**redisson批量*/
    private RBatch batch;
    /**批量操作选项*/
    private BatchOptions options = BatchOptions.defaults();

    @Resource
    private RedissonClient redisson;

    private Batch(){
        batch = redisson.createBatch(options);
    }

    /**
     * 构造方法
     * @param skipResult 是否忽略执行结果
     * @param timeout 执行命令超时时间，单位毫秒
     */
    private Batch(boolean skipResult, long timeout){
        if(skipResult){
            options.skipResult();
        }
        options.responseTimeout(timeout, TimeUnit.MILLISECONDS);
        batch = redisson.createBatch(options);
    }

    /**
     * 默认静态构造
     * @return
     */
    public static Batch of(){
        return new Batch();
    }

    /**
     * 静态构造
     *@param skipResult 是否忽略执行结果
     * @param timeout 执行命令超时时间，单位毫秒
     * @return
     */
    public static Batch of(boolean skipResult, long timeout){
        return new Batch(skipResult, timeout);
    }

    /**
     * 存入redis
     * @param key
     * @param value
     * @return
     */
    public <V> Batch set(String key, V value){
        RBucketAsync<V> bucket = batch.getBucket(key);
        bucket.setAsync(value);
        return this;
    }

    /**
     * 删除key,针对redis的String数据结构操作
     * @param key
     * @return
     */
    public <V> Batch del(String key){
        RBucketAsync<V> bucket = batch.getBucket(key);
        bucket.deleteAsync();
        return this;
    }

    /**
     * 缓存中添加元素，设置过期时间，如expiryTime不满足要求则使用默认时间
     *
     * @param key
     * @param element
     * @param expiryTime
     */
    public <T> Batch addToSet(String key, T element, Date expiryTime) {
        RSetCacheAsync<T> set = batch.getSetCache(key);
        Date now = new Date();
        long expiry = expiryTime.getTime() - now.getTime();
        expiry = expiry > 0 ? expiry : TimeUnit.MILLISECONDS.ordinal();
        set.addAsync(element, expiry, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 从集合中移除元素
     *
     * @param key
     * @param element
     */
    public <T> Batch removeFromSet(String key, T element) {
        RSetCacheAsync<T> set = batch.getSetCache(key);
        set.removeAsync(element);
        return this;
    }

    /**
     * 清除整个集合
     *
     * @param key
     */
    public <T> Batch clearSet(String key) {
        RSetCacheAsync<T> set = batch.getSetCache(key);
        set.deleteAsync();
        return this;
    }

    /**
     * 执行
     * @return
     */
    public BatchResult<?> execute(){
        return batch.execute();
    }
}