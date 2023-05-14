package com.hmdp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.entity.Blog;
import com.hmdp.entity.Shop;
import com.hmdp.service.IBlogService;
import com.hmdp.service.IShopService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.hmdp.utils.RedisConstants.CACHE_BLOG_KEY;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;

/**
 * <p>Redis缓存预热</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/5/14 23:07
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Component
public class RedisHandler implements InitializingBean {
    @Resource
    private StringRedisTemplate redisTemplate;

    @Resource
    private IShopService shopService;

    @Resource
    private IBlogService blogService;

    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化缓存
        List<Blog> blogList = blogService.list();
        for (Blog blog : blogList) {
            // 1.将数据转换为json字符串
            String json = MAPPER.writeValueAsString(blog);
            redisTemplate.opsForValue().set(CACHE_BLOG_KEY  + blog.getId(), json);
        }
        List<Shop> shopList = shopService.list();
        for (Shop shop : shopList) {
            // 1.将数据转换为json字符串
            String json = MAPPER.writeValueAsString(shop);
            redisTemplate.opsForValue().set(CACHE_SHOP_KEY  + shop.getId(), json);
        }

    }
}
