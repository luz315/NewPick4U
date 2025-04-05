package com.newpick4u.client.client.domain.entity;

import com.newpick4u.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_client")
public class Client extends BaseEntity {

  @Id
  @UuidGenerator
  @Column(name = "client_id", nullable = false)
  private UUID clientId;
  @Column(nullable = false, length = 50)
  private String name;
  @Column(nullable = false)
  private Industry industry;
  @Column(nullable = false, length = 50)
  private String email;
  @Column(nullable = false, length = 50)
  private String phone;
  @Column(nullable = false, length = 100)
  private String address;

  public static Client of(String name, Industry industry, String email, String phone,
      String address) {
    Client client = new Client();
    client.name = name;
    client.industry = industry;
    client.email = email;
    client.phone = phone;
    client.address = address;
    return client;
  }

  @Getter
  public enum Industry {
    TECHNOLOGY("기술"),
    FINANCE("금융"),
    HEALTHCARE("헬스케어"),
    EDUCATION("교육"),
    MANUFACTURING("제조"),
    TRANSPORTATION("운송"),
    REAL_ESTATE("부동산");

    private final String description;

    Industry(String description) {
      this.description = description;
    }
  }
}
