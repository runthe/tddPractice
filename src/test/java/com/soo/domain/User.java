package com.soo.domain;

/**
 * Created by soo on 2016. 11. 12..
 */
public class User {

  private String userId;

  private String userPassword;

  public User(String userId, String userPassword) {
    this.userId = userId;
    this.userPassword = userPassword;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public boolean matchPassword(String userPassword) {
    return !userPassword.equals("wrongPassword");
  }
}
