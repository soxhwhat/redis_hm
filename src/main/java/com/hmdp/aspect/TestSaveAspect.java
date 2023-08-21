package com.hmdp.aspect;

import com.hmdp.service.excel.CompanyName;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.JoinPoint;
@Aspect
@Component
@Slf4j
public class TestSaveAspect {
    //定义切点：CompanyNameService的save
    @Pointcut("execution(* com.hmdp.service.excel.CompanyNameService.save(..))")
    public void savePointcut() {
    }
    @Pointcut("execution(* com.hmdp.service.excel.CompanyNameService.updateById(..))")
    public void updatePointcut() {
    }

    //定义切面
    @AfterReturning("updatePointcut()")
    public void afterUpdate(JoinPoint joinPoint) {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 转换为CompanyName
        CompanyName companyName = (CompanyName) args[0];
        // 打印
        log.info("before update");
        log.info(companyName.toString());
        log.info("after update");
    }


    @AfterReturning("savePointcut()")
    public void afterSave(JoinPoint joinPoint) {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 转换为CompanyName
        CompanyName companyName = (CompanyName) args[0];
        // 打印
        log.info("before save");
        log.info(companyName.toString());
        log.info("after save");
    }

}
