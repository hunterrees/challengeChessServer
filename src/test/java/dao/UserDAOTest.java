package dao;

import exception.UserNotFoundException;
import model.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class UserDAOTest {

  private List<User> users;
  private User user1;
  private User user2;

  private UserDAO testModel;

  @BeforeMethod
  public void setUp() {
    testModel = new UserDAO();
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

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User user1 not found.*")
  public void shouldThrowErrorIfUserNotFoundWhenRetrieving() throws UserNotFoundException {
    testModel.addUser(user2);

    testModel.getUser("user1");
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
}