package com.closememo.command.infra.sync;

import com.closememo.command.application.ChangeCommand;
import com.closememo.command.domain.Identifier;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * multi-thread 환경에서 동기화 처리를 위해 ChangeCommand 의 hash 를 key 로 여기서 Lock 을 걸고 푼다.
 */
@Aspect
@Order(0) // @Transactional 보다 먼저 시작되어야 한다.
@Component
public class LockAspect {

  private final LockManager lockManager;

  public LockAspect(LockManager lockManager) {
    this.lockManager = lockManager;
  }

  @Around("execution(* com.closememo.command.application..*(..))" +
      " && @annotation(org.springframework.integration.annotation.ServiceActivator)")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {

    Object payload = pjp.getArgs()[0];
    if (payload instanceof ChangeCommand) {
      ChangeCommand<? extends Identifier> command = (ChangeCommand<? extends Identifier>) payload;
      Integer hash = command.getHash();

      lockManager.lock(hash);
      Object result = pjp.proceed();
      lockManager.unlock(hash);

      return result;
    } else {
      return pjp.proceed();
    }
  }
}
