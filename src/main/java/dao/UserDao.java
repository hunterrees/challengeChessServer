package dao;

import exception.user.UserNotFoundException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

  private final Map<String, User> users;
  private static UserDao userDAO = null;

  UserDao() {
    users = new HashMap<>();
  }

  public static UserDao getInstance(){
    if(userDAO == null){
      userDAO = new UserDao();
    }
    return userDAO;
  }

  /**
   * Gets all users.
   *
   * @return List<User> of all users on the server.
   */
  public List<User> getAllUsers() {
    LOGGER.info("Retrieving all users.");
    List<User> result = new ArrayList<>();
    for (Map.Entry<String, User> user : users.entrySet()) {
      result.add(user.getValue());
    }
    return result;
  }

  /**
   * Gets the user with the specified username.
   *
   * @return User object for specified user.
   * @throws UserNotFoundException if user is not found.
   */
  public User getUser(String username) throws UserNotFoundException {
    LOGGER.info("Retrieving user: {}", username);
    User result = users.get(username);
    if (result == null) {
      throw new UserNotFoundException(String.format("User %s not found", username));
    }
    return result;
  }

  /**
   * Checks if the user exists.
   *
   * @param username non-null string of user to check for.
   * @return if the user exists or not.
   */
  public boolean hasUser(String username) {
    LOGGER.info("Checking if user {} exists", username);
    return users.get(username) != null;
  }

  /**
   * Adds user to the database in the logged-in position. Username must be unique.
   * Username, password, online, and email fields are initialized; Other fields are 0/null.
   *
   * @param user non-null User to be added to the DB.
   */
  public void addUser(User user) {
    LOGGER.info("Adding user: {}", user);
    users.put(user.getUsername(), user);
  }

  /**
   * Finds matching user in DB and replaces it with the new User info. User must already exist in the database.
   * User with matching username will be updated but win record won't be affected.
   *
   * @param user non-null User to be update in the DB.
   * @throws UserNotFoundException if user does not exist.
   */
  public void updateUser(User user) throws UserNotFoundException {
    LOGGER.info("Updating user: {}", user);
    User userToUpdate = users.get(user.getUsername());
    if (userToUpdate == null) {
      throw new UserNotFoundException(String.format("User %s not found", user.getUsername()));
    }
    users.put(user.getUsername(), user);
  }
}
