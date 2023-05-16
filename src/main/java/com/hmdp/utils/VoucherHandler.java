package com.hmdp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import com.hmdp.entity.Shop;
import com.hmdp.entity.Voucher;
import lombok.SneakyThrows;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

import javax.annotation.Resource;

/**
 * <p>Canal监听器</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/5/16 16:14
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Component
// 监听的表
@CanalTable(value = "tb_voucher")
public class VoucherHandler implements EntryHandler<Voucher> {
    @Resource
    private RedisHandler redisHandler;

    @Resource
    private Cache<Long, Voucher> voucherCache;

    @SneakyThrows
    @Override
    public void insert(Voucher voucher) {
        // 新增数据到本地缓存
        voucherCache.put(voucher.getId(), voucher);
        // 新增数据到redis
        redisHandler.saveVoucher(voucher);
    }

    @SneakyThrows
    @Override
    public void update(Voucher before, Voucher after) {
        // 更新本地缓存
        voucherCache.put(after.getId(), after);
        // 更新数据到redis
        redisHandler.saveVoucher(after);
    }

    @Override
    public void delete(Voucher voucher) {
        // 删除本地缓存
        voucherCache.invalidate(voucher.getId());
        // 删除数据到redis
        redisHandler.deleteItemById(voucher.getId());

    }
}
