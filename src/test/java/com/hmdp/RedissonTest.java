package com.hmdp;

import com.alibaba.fastjson.JSON;
import com.hmdp.service.IBlogService;
import com.hmdp.utils.Batch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.BatchResult;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IBlogService blogService;

    private RLock lock;

    @BeforeEach
    void setUp() {
        lock = redissonClient.getLock("order");
    }

    @Test
    void method1() throws InterruptedException {
        // 尝试获取锁，此锁是可重入锁 tryLock(long waitTime, long leaseTime, TimeUnit unit)参数分别为等待时间，租约时间，时间单位。此处设置最长等待一秒钟
        boolean isLock = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!isLock) {
            log.error("获取锁失败 .... 1");
            return;
        }
        try {
            log.info("获取锁成功 .... 1");
            method2();
            log.info("开始执行业务 ... 1");
        } finally {
            log.warn("准备释放锁 .... 1");
            lock.unlock();
        }
    }
    void method2() {
        // 尝试获取锁
        boolean isLock = lock.tryLock();
        if (!isLock) {
            log.error("获取锁失败 .... 2");
            return;
        }
        try {
            log.info("获取锁成功 .... 2");
            log.info("开始执行业务 ... 2");
        } finally {
            log.warn("准备释放锁 .... 2");
            lock.unlock();
        }
    }

    @Test
    public void testLike() {
        blogService.likeBlog(7L);
    }

    public static final int STR_MAX_LEN = 10 * 1024;
    public static final int HASH_MAX_LEN = 500;

    /**
     * Scan实现扫描bigkey
     */
    @Test
    public void testScan() {
//        int maxLen = 0;
//        long len = 0;
//
//        String cursor = "0";
//
//        do {
//            // 扫描并获取一部分key
//            RKeys keys = redissonClient.getKeys();
//
//        }
    }

    /**
     * pipeline批量操作
     */
    @Test
    public void batchTest() throws InterruptedException{
        Batch batch = Batch.of(false, 3000);
        Date date = new Date();
        batch.addToSet("TEST_SET_KEY", 1, new Date(date.getTime() + 10000));
        batch.set("TEST_STRING_KEY", "test");
        BatchResult result = batch.execute();
        System.out.println(JSON.toJSONString(result));
    }

}
