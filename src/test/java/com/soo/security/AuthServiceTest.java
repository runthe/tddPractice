package com.soo.security;

import com.soo.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

  public static final String USER_PASSWORD = "userPassword";

  public static final String NO_USER_ID = "noUserId";

  public static final String USER_ID = "userId";

  private AuthService authService;

  private UserRepository mockUserRepository;

  /*
    + 테스트 클래스 만들기 O
    + 객체 생성하기(쉬운) O
    + ID 값이 비정상적인 경우(쉬운 정상에서 벗어난) O
    + PW 값이 비정상적인 경우(쉬운 정상에서 벗어난) O
    + User가 존재하지 않는경우(정상에서 벗어난)
    + ID에 해당하지 않는 User가 존재하는데, PW가 일치하지 않는 경우(정상에서 벗어난)
    + ID와 PW가 일치하는 경우(정상)
    - 인증 정보를 리턴
   */

  @Before
  public void setup() {
    mockUserRepository = mock(UserRepository.class);
    authService = new AuthService();
    authService.setUserRepository(mockUserRepository);
  }

  @Test
  public void givenInvalidId_throwIllegalArgEx() throws Exception {
    assertIllegalArgExThrown("", USER_PASSWORD);
    assertIllegalArgExThrown(null, USER_PASSWORD);
    assertIllegalArgExThrown(USER_ID, "");
    assertIllegalArgExThrown(USER_ID, null);
  }

  @Test
  public void whenUserNotFound_thrownNonExistingUserEx() throws Exception {
    assertExceptionThrown(NO_USER_ID, USER_PASSWORD, NonExistingUserException.class);

    for (int i = 0; i < 100; i++) {
      assertExceptionThrown(NO_USER_ID + 1, USER_PASSWORD, NonExistingUserException.class);
    }
  }

  @Test
  public void whenUserFoundButWrongPw_thrownWrongPasswordEx() throws Exception {
    givenUserExists(USER_ID, USER_PASSWORD);
    assertExceptionThrown(USER_ID, "wrongPassword", WrongPasswordException.class);
    verifyUserFound(USER_ID);
  }

  @Test
  public void whenUserFoundAndRightPw_returnAuth() throws Exception {
    givenUserExists(USER_ID, USER_PASSWORD);
    Authentication auth = authService.authenticate(USER_ID, USER_PASSWORD);
    assertThat(auth.getId(), equalTo(USER_ID));
  }

  private void givenUserExists(String userId, String userPassword) {
    when(mockUserRepository.findById(userId)).thenReturn(new User(userId, userPassword));
  }

  private void verifyUserFound(String userId) {
    //해당 구문이 호출 되었는지 확인
    verify(mockUserRepository).findById(userId);
  }

  private void assertIllegalArgExThrown(String userId, String userPassword) {
    assertExceptionThrown(userId, userPassword, IllegalArgumentException.class);
  }

  private void assertExceptionThrown(String userId, String userPassword, Class<? extends Exception> type) {
    Exception thrownEx = null;

    try {
      authService.authenticate(userId, userPassword);
    } catch (Exception e) {
      thrownEx = e;
    }

    assertThat(thrownEx, instanceOf(type));
  }

  private class AuthService {

    public Authentication authenticate(String userId, String userPassword) {
      assertIdAndPw(userId, userPassword);

      User user = findByUserId(userId);

      findUserOrThrownNonUserEx(user);
      throwExIfPasswordWrong(userPassword, user);

      return createAuthentication(user);
    }

    private Authentication createAuthentication(User user) {
      return new Authentication(user.getUserId());
    }

    private void findUserOrThrownNonUserEx(User user) {
      if (user == null) {
        throw new NonExistingUserException();
      }
    }

    private void throwExIfPasswordWrong(String userPassword, User user) {
      if (!user.matchPassword(userPassword)) {
        throw new WrongPasswordException();
      }
    }

    private void assertIdAndPw(String userId, String userPassword) {
      if (StringUtils.isBlank(userId)) {
        throw new IllegalArgumentException();
      }

      if (StringUtils.isBlank(userPassword)) {
        throw new IllegalArgumentException();
      }
    }

    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
      this.userRepository = userRepository;
    }

    private User findByUserId(String userId) {
      return userRepository.findById(userId);
    }
  }
}