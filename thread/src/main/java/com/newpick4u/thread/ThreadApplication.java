package com.newpick4u.thread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ThreadApplication {

  public static void main(String[] args) {
    SpringApplication.run(ThreadApplication.class, args);
  }

}
