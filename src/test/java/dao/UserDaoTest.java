package dao;

import exception.user.UserNotFoundException;
import model.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class UserDaoTest {

  private List<User> users;
  private User user1;
  private User user2;

  private UserDao testModel;

  @BeforeMethod
  public void setUp() {
    testModel = new UserDao();

    users = new ArrayList<>();
    user1 = new User("user1", "password1", "email1");
    user2 = new User("user2", "password2", "email2");

    users.add(user1);
    users.add(user2);
  }

  @Test
  public void shouldGetAllUsers() {
    testModel.addUser(user1);
    testModel.addUser(user2);

    List<User> result = testModel.getAllUsers();

    assertEquals(result, users);
  }

  @Test
  public void shouldGetASingleUser() throws UserNotFoundException {
    testModel.addUser(user1);

    User result = testModel.getUser("user1");

    assertEquals(result, user1);
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User user2 not found.*")
  public void shouldThrowErrorIfUserNotFoundWhenRetrieving() throws UserNotFoundException {
    testModel.addUser(user1);

    testModel.getUser("user2");
  }

  @Test
  public void shouldAddUser() throws UserNotFoundException {
    testModel.addUser(user1);

    User result = testModel.getUser("user1");

    assertEquals(result, user1);
  }

  @Test
  public void shouldUpdateUser() throws UserNotFoundException {
    testModel.addUser(user1);

    user1.setEmail("newEmail");

    testModel.updateUser(user1);

    User result = testModel.getUser("user1");

    assertEquals(result, user1);
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User user1 not found.*")
  public void shouldThrowErrorIfUserNotFoundWhenUpdating() throws UserNotFoundException {
    testModel.addUser(user2);

    user1.setEmail("newEmail");

    testModel.updateUser(user1);
  }

  @Test
  public void shouldReturnTrueIfUserExists() {
    testModel.addUser(user1);
    boolean result = testModel.hasUser("user1");
    assertEquals(result, true);
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotExist() {
    boolean result = testModel.hasUser("user2");
    assertEquals(result, false);
  }
}
