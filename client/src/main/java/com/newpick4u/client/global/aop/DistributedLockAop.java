package com.newpick4u.client.global.aop;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

  private static final String REDISSON_LOCK_PREFIX = "LOCK:";

  private final RedissonClient redissonClient;

  @Around("@annotation(distributedLock)")
  public Object lock(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock)
      throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    Object parsedKey = CustomSpringELParser.getDynamicValue(signature.getParameterNames(),
        joinPoint.getArgs(), distributedLock.key());
    String key = REDISSON_LOCK_PREFIX + parsedKey;
    RLock rLock = redissonClient.getLock(key);

    try {
      boolean available = rLock.tryLock(
          distributedLock.waitTime(),
          distributedLock.leaseTime(),
          distributedLock.timeUnit()
      );

      if (!available) {
        log.warn("분산 락 획득에 실패하였습니다 : {}", key);
        return false;
      }

      return joinPoint.proceed();

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw e;
    } finally {
      try {
        rLock.unlock();
      } catch (IllegalMonitorStateException e) {
        log.info("분산 락이 이미 해제된 상태입니다 : {}, key: {}",
            method.getName(), key);
      }
    }
  }

  public static class CustomSpringELParser {

    private CustomSpringELParser() {
    }

    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
      ExpressionParser parser = new SpelExpressionParser();
      StandardEvaluationContext context = new StandardEvaluationContext();

      for (int i = 0; i < parameterNames.length; i++) {
        context.setVariable(parameterNames[i], args[i]);
      }
      Expression expression = parser.parseExpression(key);

      return parser.parseExpression(key).getValue(context, Object.class);
    }
  }
}
