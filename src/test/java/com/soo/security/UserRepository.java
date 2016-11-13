package com.soo.security;

import com.soo.domain.User;

public interface UserRepository {

  User findById(String userId);
}
