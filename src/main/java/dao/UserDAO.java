package dao;

import exception.UserNotFoundException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

  private Map<String, User> users;

  public UserDAO() {
    users = new HashMap<>();
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

  public boolean hasUser(String username) {
    LOGGER.info("Checking if user {} exists", username);
    return users.get(username) != null;
  }

  /**
   * Adds user to the database in the logged-in position. Username must be unique.
   * Username, password, online, and email fields are initialized; Other fields are 0/null.
   */
  public void addUser(User user) {
    LOGGER.info("Adding user: {}", user);
    users.put(user.getUsername(), user);
  }

  /**
   * Finds matching user in DB and replaces it with the new User info. User must already exist in the database.
   * User with matching username will be updated but win record won't be affected.
   *
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
