package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.enumeration.OperationType;
import com.sky.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.meta.Exclusive;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
@Slf4j
@Aspect
public class AutoFillAspect {

    // 定义拦截规则
    @Pointcut("execution(* com.sky.service.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFill(){}

    /**
     * 前置通知，在方法执行前执行, 进行公共字段填充
     * @param joinPoint
     */
    @Before("autoFill()")
    public void before(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            log.error("公共字段填充失败,没有参数");
            return;
        }
        Object arg = args[0];
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
        try {
            if (autoFill.value().equals(OperationType.INSERT)){
                log.info("开始进行数据插入");
                Method setCreateUser = arg.getClass().getMethod("setCreateUser", Long.class);
                setCreateUser.invoke(arg, Long.valueOf( CurrentHolder.getCurrentId()));
                Method setCreateTime = arg.getClass().getMethod("setCreateTime", LocalDateTime.class);
                setCreateTime.invoke(arg, LocalDateTime.now());
            }
            if (autoFill.value().equals(OperationType.UPDATE)){
                log.info("开始进行数据更新");
                Method setUpdateTime = arg.getClass().getMethod("setUpdateTime", LocalDateTime.class);
                setUpdateTime.invoke(arg, LocalDateTime.now());
                Method setUpdateUser = arg.getClass().getMethod("setUpdateUser", Long.class);
                setUpdateUser.invoke(arg, Long.valueOf(CurrentHolder.getCurrentId()));
            }
        } catch (Exception e) {
            log.error("自动填充失败");
            e.printStackTrace();
        }

    }



}
