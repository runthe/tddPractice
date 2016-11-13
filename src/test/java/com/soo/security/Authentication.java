package com.soo.security;

public class Authentication {

  private String id;

  public Authentication(String userId) {
    this.id = userId;
  }

  public String getId() {
    return id;
  }
}
