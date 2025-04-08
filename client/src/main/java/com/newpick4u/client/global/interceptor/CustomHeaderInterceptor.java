package com.newpick4u.client.global.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CustomHeaderInterceptor implements RequestInterceptor {

  private static final String USER_ID_HEADER = "X-User-Id";
  private static final String USER_ROLE_HEADER = "X-User-Role";

  @Override
  public void apply(RequestTemplate requestTemplate) {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      String userId = request.getHeader(USER_ID_HEADER);
      String userRoles = request.getHeader(USER_ROLE_HEADER);

      if (userId != null && userRoles != null) {
        requestTemplate.header(USER_ID_HEADER, userId);
        requestTemplate.header(USER_ROLE_HEADER, userRoles);
      }
    }
  }
}
