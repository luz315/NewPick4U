package com.newpick4u.common.aop;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class TracingAspect {

  private final Tracer tracer;

  @Around(
      "@within(org.springframework.stereotype.Service) "
          + "|| @within(org.springframework.stereotype.Repository) "
          + "|| @annotation(org.springframework.transaction.annotation.Transactional) "
          + "|| @annotation(org.springframework.kafka.annotation.KafkaListener)"
  )
  public Object traceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    // 메서드 시그니처 기반 스팬 이름
    String spanName = joinPoint.getSignature().toShortString();
    Span span = tracer.nextSpan().name(spanName).start();

    // KafkaListener인 경우 topic 정보와 peer.service 태그 추가
    MethodSignature sig = (MethodSignature) joinPoint.getSignature();
    KafkaListener listener = sig.getMethod().getAnnotation(KafkaListener.class);
    if (listener != null) {
      // 첫 번째 topic을 destination으로 예시 사용
      String topic = listener.topics().length > 0
          ? listener.topics()[0]
          : "unknown";
      span.tag("messaging.system", "kafka");
      span.tag("messaging.destination", topic);
      // Zipkin Dependencies 뷰에 표시하려면 peer.service 태그 필수
      span.tag("peer.service", "kafka");
    }

    try (Tracer.SpanInScope scope = tracer.withSpan(span)) {
      log.debug("Tracing span started: {}", spanName);
      return joinPoint.proceed();
    } catch (Exception e) {
      span.error(e);
      throw e;
    } finally {
      span.end();
      log.debug("Tracing span ended: {}", spanName);
    }
  }
}
