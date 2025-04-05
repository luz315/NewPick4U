package com.newpick4u.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  @CreatedBy
  @Column(updatable = false)
  private Long createdBy;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedBy
  private Long updatedBy;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  private Long deletedBy;


  private LocalDateTime deletedAt;

  public void delete(LocalDateTime deleteAt, Long deletedBy) {
    this.deletedAt = deleteAt;
    this.deletedBy = deletedBy;
  }

  public void createdBy(Long createdBy) {
    this.createdBy = createdBy;
  }
}
