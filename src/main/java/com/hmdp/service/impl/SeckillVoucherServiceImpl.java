package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.mapper.SeckillVoucherMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀优惠券表，与优惠券是一对一关系 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-01-04
 */
@Service
public class SeckillVoucherServiceImpl extends ServiceImpl<SeckillVoucherMapper, SeckillVoucher> implements ISeckillVoucherService {

    @Autowired
    private SeckillVoucherMapper seckillVoucherMapper;
    /**
     * 更新库存
     * 采用lambdaUpdateChainWrapper和setSql方法直接设置字段为待修改的值
     * @param id
     * @return
     */
    @Override
    public boolean updateStock(Long id) {
//        return new LambdaUpdateChainWrapper<SeckillVoucher>(seckillVoucherMapper)
//                .eq(SeckillVoucher::getVoucherId, id)
//                .gt(SeckillVoucher::getStock, 0)
//                .setSql("stock = stock - 1")
//                .update();
        return lambdaUpdate()
                .eq(SeckillVoucher::getVoucherId, id)
                /*
                  乐观锁乐观锁可以避免悲观锁的性能问题，但是会有更新成功率太低的问题
                  此处可以通过更改判断库存是否大于0来解决，因为我们并不是真正的更新库存，而是减库存，仅仅是为了判断库存是否大于0
                 */
                .gt(SeckillVoucher::getStock, 0)
                .setSql("stock = stock - 1")
                .update();
    }
}
