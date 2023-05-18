package com.hmdp;

import com.hmdp.config.RedissonConfig;
import com.hmdp.utils.RedisHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * <p>借助布隆过滤器解决缓存穿透</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/5/18 10:46
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Slf4j
@SpringBootTest
public class RedissonBloomFilter {
    @Resource
    private RedissonClient redissonClient;

    @Test
    public void bloomFilterDemo() {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("bloomFilter");
        // 初始化，容器中预计元素为100000000L,误差率为3%
        bloomFilter.tryInit(100000000L, 0.03);
        // 将值放入到容器中
        for (int i = 0; i < 10000; i++) {
            bloomFilter.add("soxhwhat" + i);
        }
        // 用来统计误判的值
        int count = 0;
        //查询不存在的数据一千次
        for (int i = 0; i < 1000; i++) {
            if (bloomFilter.contains("xiaocheng" + i)) {
                count++;
            }
        }
        System.out.println("判断错误的个数："+count);
        System.out.println("soxhwhat9999是否在过滤器中存在："+bloomFilter.contains("soxhwhat9999"));
        System.out.println("soxhwhat11111是否在过滤器中存在："+bloomFilter.contains("soxhwhat11111"));
        System.out.println("预计插入数量：" + bloomFilter.getExpectedInsertions());
        System.out.println("容错率：" + bloomFilter.getFalseProbability());
        System.out.println("hash函数的个数：" + bloomFilter.getHashIterations());
        System.out.println("插入对象的个数：" + bloomFilter.count());
    }


}
