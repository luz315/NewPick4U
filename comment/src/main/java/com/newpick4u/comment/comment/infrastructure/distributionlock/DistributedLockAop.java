package com.newpick4u.comment.comment.infrastructure.distributionlock;

import com.newpick4u.comment.global.exception.CommentException;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class DistributedLockAop {

  private static final String REDISSON_LOCK_PREFIX = "LOCK:comment:";

  private final RedissonClient redissonClient;

  @Around("@annotation(com.newpick4u.comment.comment.infrastructure.distributionlock.DistributedLock)")
  public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

    String key =
        REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(),
            joinPoint.getArgs(), distributedLock.key());
    RLock rLock = redissonClient.getLock(key);

    try {
      boolean available = rLock.tryLock(
          distributedLock.waitTime(),
          distributedLock.leaseTime(),
          distributedLock.timeUnit()
      );
      if (!available) {
        throw new CommentException.ProcessFailException();
      }

      return joinPoint.proceed();

    } catch (InterruptedException e) {
      log.error("lock process interrupted", e);
      throw new InterruptedException();
    } finally {
      try {
        rLock.unlock();
      } catch (IllegalMonitorStateException e) {
        log.warn("Redisson Lock Already UnLock {} {}",
            Map.of("serviceName", method.getName()),
            Map.of("key", key)
        );
      }
    }
  }
}
