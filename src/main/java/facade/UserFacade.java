package facade;

import exception.InvalidUserCookieException;
import exception.InvalidPasswordException;
import exception.UserNotFoundException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class UserFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);

  /**
   * @Pre None.
   * @return List<User> of all users currently registered (see User class).
   */
  public List<User> getAllUsers() {
    return null;
  }

  /**
   * Get specific user.
   *
   * @Pre User must exist.
   * @return Returns User object for specified user.
   * @throws UserNotFoundException if user is not found.
   * @throws InvalidUserCookieException if the cookie is invalid.
   */
  public User getUser(String username, String cookie) throws UserNotFoundException, InvalidUserCookieException {
    return null;
  }

  /**
   * Checks that user does not exist already and adds it to the database in the logged in position.
   *
   * @Pre Username is unique; username, password, online, and email fields are initialized; other fields are 0/null.
   * @Post User object is in database, user cookie is created.
   * @return User cookie.
   */
  public String register(User basicUser) {
    return null;
  }

  /**
   *
   * @Pre Username and password are not null. Password is in specified encrypted format.
   * @Post Verifies that user exists, validates password, sets user to online, returns user cookie if it does exist.
   * @return User cookie if user exists and password is verified.
   * @throws UserNotFoundException if user is not found.
   * @throws InvalidPasswordException if password is not verified.
   */
  public String login(String username, String password) throws UserNotFoundException, InvalidPasswordException {
    return null;
  }

  /**
   *
   * @Pre User with matching username already exists.
   * @Post Sets user to offline.
   * @throws InvalidUserCookieException if the cookie is invalid.
   */
  public void logout(String username, String cookie) throws InvalidUserCookieException {
  }

  /**
   * Finds matching user in DB and replaces it with the new User info.
   *
   * @Pre User is non null.
   * @Post User with matching username will be updated but win record won't be affected.
   * @throws UserNotFoundException if user does not exist.
   * @throws InvalidUserCookieException if the cookie is invalid.
   *
   */
  public void updateUser(User user, String cookie) throws UserNotFoundException, InvalidUserCookieException {
  }
}
