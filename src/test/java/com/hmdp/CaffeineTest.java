package com.hmdp;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * <p>Caffeine本地缓存</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/5/14 11:50
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
public class CaffeineTest {
    @Test
    void testBasicOps() {
        Cache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();

        // 存数据
        cache.put("name", "虎哥");
        // 取数据
        cache.getIfPresent("name");

        // 取数据，如果没有，就执行后面的方法，然后将返回值写入缓存
        String name = cache.get("name", key -> {
            // 从数据库中查询
            return "虎哥";
        });
    }
}
