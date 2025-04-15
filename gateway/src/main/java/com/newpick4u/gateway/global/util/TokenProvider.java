package com.newpick4u.gateway.global.util;

import static com.newpick4u.common.exception.CustomException.from;

import com.newpick4u.common.exception.CustomException;
import com.newpick4u.gateway.global.exception.GatewayErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

  public static final String TOKEN_PREFIX = "Bearer ";

  private SecretKey key;
  @Value("${jwt.secret}")
  private String secretKey;

  public boolean validAccessToken(String token) {
    if (!StringUtils.hasText(token) || !token.startsWith(TOKEN_PREFIX)) {
      return false;
    }

    return validateToken(token.substring(TOKEN_PREFIX.length()));
  }

  private boolean validateToken(String token) {
    return !getExpiration(token).before(new Date());
  }

  private Date getExpiration(String token) {
    if (!StringUtils.hasText(token)) {
      throw CustomException.from(GatewayErrorCode.EXPIRED_JWT_TOKEN);
    }

    return this.parseClaims(token).getExpiration();
  }

  public String getUserId(String token) {
    Long userId = this.parseClaims(token).get("userId", Long.class);
    return userId.toString();
  }

  public String getUserRole(String token) {
    return this.parseClaims(token).get("userRole", String.class);
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      throw CustomException.from(
          GatewayErrorCode.INVALID_JWT_SIGNATURE);
    } catch (ExpiredJwtException e) {
      throw from(GatewayErrorCode.EXPIRED_JWT_TOKEN);
    } catch (UnsupportedJwtException e) {
      throw from(GatewayErrorCode.UNSUPPORTED_JWT_TOKEN);
    } catch (IllegalArgumentException e) {
      throw from(GatewayErrorCode.JWT_CLAIMS_EMPTY);
    }
  }

  @PostConstruct
  private void setKey() {
    key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secretKey));
  }
}