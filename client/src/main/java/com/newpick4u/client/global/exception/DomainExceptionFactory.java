package com.newpick4u.client.global.exception;

import com.newpick4u.common.exception.CustomException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class DomainExceptionFactory {

  private static final Map<String, CustomException> EXCEPTION_CACHE = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static <T extends CustomException> T getDomainException(Class<T> clazz) {
    String key = clazz.getName();

    return (T) EXCEPTION_CACHE.computeIfAbsent(key, k -> createExceptionInstance(clazz));
  }

  private static <T extends CustomException> CustomException createExceptionInstance(
      Class<T> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (NoSuchMethodException | InstantiationException |
             IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("예외 인스턴스를 생성할 수 없습니다: " + clazz.getName(), e);
    }
  }

}
