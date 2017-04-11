package facade;

import dao.UserDao;
import exception.user.*;
import manager.CookieManager;
import model.User;
import model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);

  private final UserDao userDao;
  private final CookieManager cookieManager;

  public UserFacade() {
    userDao = UserDao.getInstance();
    cookieManager = CookieManager.getInstance();
  }

  UserFacade(UserDao userDAO, CookieManager cookieManager) {
    this.userDao = userDAO;
    this.cookieManager = cookieManager;
  }

  /**
   * @Pre None.
   * @return List<String> of all usernames currently registered.
   */
  public List<String> getAllUsers() {
    LOGGER.info("UserFacade.getAllUsers");
    List<String> usernames = new ArrayList<>();
    List<User> users = userDao.getAllUsers();
    for (User u : users) {
      usernames.add(u.getUsername());
    }
    return usernames;
  }

  /**
   * Get specific user.
   *
   * @Pre User must exist.
   * @return Returns UserInfo object for specified user.
   * @throws UserNotFoundException      if user is not found.
   * @throws InvalidUserCookieException if the cookie is invalid.
   */
  public UserInfo getUser(String username, String cookie) throws UserNotFoundException, InvalidUserCookieException {
    LOGGER.info("UserFacade.getUser with username {} and cookie {}", username, cookie);
    cookieManager.validateUserCookie(cookie);
    return new UserInfo(userDao.getUser(username));
  }

  /**
   * Checks that user does not exist already and adds it to the database in the logged in position.
   *
   * @return User cookie.
   * @throws UserAlreadyExistsException if that username is already taken
   * @throws UserException              if username has a ':' in it or the data members of the User are not already initialized
   * @Pre Username is unique; username, password, online, and email fields are initialized; other fields are 0/null.
   * @Post User object is in database, user cookie is created.
   */
  public String register(User basicUser) throws UserException {
    LOGGER.info("UserFacade.register with {}", basicUser);
    if (basicUser.getUsername() == null || basicUser.getPassword() == null || basicUser.getEmail() == null) {
      throw new UserException("User info not initialized");
    }
    if (basicUser.getUsername().indexOf(':') != -1) {
      throw new UserException(": not allowed in username");
    }
    if (!userDao.hasUser(basicUser.getUsername())) {
      LOGGER.info("User is being added.");
      basicUser.setOnline();
      userDao.addUser(basicUser);
    }
    else {
      throw new UserAlreadyExistsException("User already exists");
    }
    return cookieManager.makeUserCookie(basicUser.getUsername());
  }

  /**
   *
   * @Pre Username and password are not null. Password is in specified encrypted format.
   * @Post Verifies that user exists, validates password, sets user to online, returns user cookie if it does exist.
   * @return User cookie if user exists and password is verified.
   * @throws UserNotFoundException    if user is not found.
   * @throws InvalidPasswordException if password is not verified.
   */
  public String login(String username, String password) throws UserNotFoundException, InvalidPasswordException {
    LOGGER.info("UserFacade.login with username {}", username);
    User tempUser = userDao.getUser(username);
    if (!tempUser.getPassword().equals(password)) {
      throw new InvalidPasswordException("Invalid Password");
    }
    else {
      LOGGER.info("Logging user in.");
      tempUser.setOnline();
      userDao.updateUser(tempUser);
    }
    return cookieManager.makeUserCookie(username);
  }

  /**
   * @Pre User with matching username already exists.
   * @Post Sets user to offline.
   * @throws InvalidUserCookieException if the cookie is invalid.
   *
   */
  public void logout(String username, String cookie) throws InvalidUserCookieException, UserNotFoundException {
    LOGGER.info("UserFacade.logout with username {} and cookie {}", username, cookie);
    cookieManager.validateUserCookie(cookie);
    User tempUser = userDao.getUser(username);
    tempUser.setOffline();
    userDao.updateUser(tempUser);
    LOGGER.info("Successfully logged {} out", username);
  }

  /**
   * Finds matching user in DB and replaces it with the new User info.
   *
   * @Pre User is non null. User cookie must be for the user attempting to be updated
   * @Post User with matching username will be updated but win record won't be affected.
   * @throws UserNotFoundException      if user does not exist.
   * @throws InvalidUserCookieException if the cookie is invalid or does not match the user.
   *
   */
  public void updateUser(User user, String cookie) throws UserNotFoundException, InvalidUserCookieException {
    LOGGER.info("UserFacade.updateUser with user {}and cookie {}", user, cookie);
    cookieManager.validateUserCookie(cookie);
    String cookieUsername = cookie.substring(0, cookie.indexOf(':'));
    if (!cookieUsername.equals(user.getUsername())) {
      throw new InvalidUserCookieException("Cookie does not match user to update");
    }
    userDao.updateUser(user);
  }
}
