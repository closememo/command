package com.closememo.command.infra.projection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WaitForProjectionAspect {

  @Around("execution(* com.closememo.command.interfaces..*(..)) "
      + "&& @annotation(com.closememo.command.infra.projection.WaitForProjection)")
  public Object aroundWaitForProjection(ProceedingJoinPoint joinPoint) throws Throwable {
    Object result = joinPoint.proceed();

    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    WaitForProjection waitForProjection = AnnotationUtils.getAnnotation(methodSignature.getMethod(), WaitForProjection.class);
    Thread.sleep(waitForProjection.timeout());

    return result;
  }
}
