package api;

import exception.*;
import facade.UserFacade;
import model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class UserEndpointTest {

  @Mock
  private UserFacade mockUserFacade;

  private List<User> users;
  private User user1;
  private User user2;
  private User user3;

  private final static String VALID_COOKIE = "validCookie";
  private final static String INVALID_COOKIE = "invalidCookie";

  private UserEndpoint testModel;

  @BeforeMethod
  public void setUp() throws UserException {
    MockitoAnnotations.initMocks(this);
    users = new ArrayList<>();
    user1 = new User("user1", "password1", "email1");
    user2 = new User("user2", "password2", "email2");
    user3 = new User("user3", "password3", "email3");

    users.add(user1);
    users.add(user2);

    when(mockUserFacade.getAllUsers()).thenReturn(users);
    when(mockUserFacade.getUser("user1", VALID_COOKIE)).thenReturn(user1);
    when(mockUserFacade.getUser("user2", VALID_COOKIE)).thenReturn(user2);
    doThrow(new InvalidUserCookieException("Invalid Cookie")).when(mockUserFacade).getUser(anyString(), eq(INVALID_COOKIE));
    doThrow(new InvalidUserCookieException("Invalid Cookie")).when(mockUserFacade).logout(anyString(), eq(INVALID_COOKIE));
    doThrow(new InvalidUserCookieException("Invalid Cookie")).when(mockUserFacade).updateUser(any(), eq(INVALID_COOKIE));
    testModel = new UserEndpoint(mockUserFacade);
  }

  @Test
  public void shouldGetAllUsers() {
    List<User> result = testModel.getAllUsers();
    assertEquals(result, users);
  }

  @Test
  public void shouldGetASingleUser() throws UserException {
    User result = testModel.getUserInfo("user1", VALID_COOKIE);
    assertEquals(result, user1);

    result = testModel.getUserInfo("user2", VALID_COOKIE);
    assertEquals(result, user2);
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Exception expected.*")
  public void shouldNotWrapUserExceptions() throws UserException {
    doThrow(new UserNotFoundException("User Exception expected")).when(mockUserFacade).login("user3", "password3");
    testModel.login("user3", user3);
  }

  @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid Cookie.*")
  public void shouldNotAllowUserAccessWithInvalidCookie() throws UserException {
    testModel.getUserInfo("user2", INVALID_COOKIE);
  }

  @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid Cookie.*")
  public void shouldNotAllowUserToModifyWithInvalidCookie() throws UserException {
    testModel.updateUserInfo(user2, INVALID_COOKIE);
  }

  @Test
  public void shouldAllowUserToModifyWithValidCookie() throws UserException {
    testModel.updateUserInfo(user1, VALID_COOKIE);
  }

  @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid Cookie.*")
  public void shouldNotAllowUserToLogoutWithInvalidCookie() throws UserException {
    testModel.logout("user2", INVALID_COOKIE);
  }

  @Test
  public void shouldAllowUserToLogoutWithValidCookie() throws UserException {
    testModel.logout("user1", VALID_COOKIE);
  }

  @Test (expectedExceptions = ServerException.class, expectedExceptionsMessageRegExp = ".*Server Exception expected.*")
  public void shouldWrapExceptionsInServerException() {
    doThrow(new NullPointerException("Server Exception expected")).when(mockUserFacade).getAllUsers();
    testModel.getAllUsers();
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Not Found.*")
  public void shouldThrowUserExceptionWhenUserDoesNotExist() throws UserException {
    doThrow(new UserNotFoundException("User Not Found")).when(mockUserFacade).login("user3", "password3");
    testModel.login("user3", user3);
  }

  @Test (expectedExceptions = InvalidPasswordException.class, expectedExceptionsMessageRegExp = ".*Invalid Password.*")
  public void shouldThrowExceptionWhenPasswordsDoNotMatch() throws UserException {
    doThrow(new InvalidPasswordException("Invalid Password")).when(mockUserFacade).login("user2", "password3");
    testModel.login("user2", user3);
  }

  @Test
  public void shouldRegisterSuccessfully() {
    testModel.register(user3);
  }

}
