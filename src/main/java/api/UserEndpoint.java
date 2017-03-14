package api;

import exception.ServerException;
import exception.user.UserException;
import facade.UserFacade;
import model.User;
import model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@EnableAutoConfiguration
@RequestMapping ("users")
class UserEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);

  private final static String USER_COOKIE = "userCookie";

  private UserFacade userFacade;

  /**
   * Default Constructor.
   */
  UserEndpoint() {
    userFacade = new UserFacade();
  }

  /**
   * Constructor for unit-testing.
   *
   * @param userFacade User Facade to hit.
   */
  UserEndpoint(UserFacade userFacade) {
    this.userFacade = userFacade;
  }

  /**
   * Gets all users.
   *
   * @return List<User> of all users on the server.
   * @throws ServerException when an unexpected error occurs.
   */
  @RequestMapping(method=RequestMethod.GET)
  List<User> getAllUsers() throws ServerException {
    List<User> result;
    try {
      LOGGER.info("/users GET hit");
      result = userFacade.getAllUsers();
    } catch (RuntimeException e) {
      LOGGER.error("Error in /users GET {}", e);
      throw new ServerException(e);
    }
    return result;
  }

  /**
   * Gets the user specified.
   * If the user doesn't exist, null is returned (Status Code 404).
   * If cookie is invalid (Status Code 403).
   *
   * @param username non-null String with the name of user to retrieve.
   * @return User that was requested.
   * @throws UserException when user cookie is not valid or user is not found.
   * @throws ServerException when an unexpected error occurs.
   */
  @RequestMapping(value="{username}", method=RequestMethod.GET)
  UserInfo getUserInfo(@PathVariable String username,
                       @RequestParam(USER_COOKIE) String cookie) throws UserException, ServerException {
    UserInfo result;
    try {
      LOGGER.info("/users/{username} GET hit with username {} and cookie {}", username, cookie);
      User user = userFacade.getUser(username, cookie);
      result = new UserInfo(user);
    } catch (RuntimeException e) {
      LOGGER.error("Error in /users/{username} GET {}", e);
      throw new ServerException(e);
    }
    return result;
  }

  /**
   * Registers the user on the server.
   * If successful, returns the created user (Status Code 201).
   *
   * @param user non-null User with the information of the user to add to system.
   * @return User that was created.
   * @throws ServerException when an unexpected error occurs.
   */
  @RequestMapping(value="{username}", method=RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  String register(@RequestBody User user) throws ServerException {
    String result;
    try {
      LOGGER.info("/users/{username} POST hit with user {}", user);
      result = userFacade.register(user);
    } catch (RuntimeException e) {
      LOGGER.error("Error in /users/{username} POST {}", e);
      throw new ServerException(e);
    }
    return result;
  }

  /**
   * Updates the users information on the server.
   * If successful (Status Code 200).
   * If the user doesn't exist (Status Code 404).
   * If cookie is invalid (Status Cod 403).
   *
   * @param user non-null User with the information of the user to update.
   * @throws UserException when user cookie is not valid or user is not found.
   * @throws ServerException when an unexpected error occurs.
   */
  @RequestMapping(value="{username}", method=RequestMethod.PUT)
  void updateUserInfo(@RequestBody User user,
                      @RequestParam(USER_COOKIE) String cookie) throws UserException, ServerException {
    try {
      LOGGER.info("/users/{username} PUT hit with user {} and cookie {}", user, cookie);
      userFacade.updateUser(user, cookie);
    } catch (RuntimeException e) {
      LOGGER.error("Error in /users/{username} PUT {}", e);
      throw new ServerException(e);
    }
  }

  /**
   * Logs in the user specified.
   * If successful, returns the logged-in user (Status Code 200).
   * If password don't match, returns null (Status Code 400).
   * If user doesn't exist, returns null (Status Code 404).
   *
   * @param username non-null String with the name of user to login.
   * @return User that was logged-in.
   * @throws UserException when user is not found.
   * @throws ServerException when an unexpected error occurs.
   */
  @RequestMapping(value="login/{username}", method=RequestMethod.PUT)
  String login(@PathVariable String username, @RequestBody User user) throws UserException, ServerException, NoSuchAlgorithmException {
    String result;
    try {
      LOGGER.info("/users/login/{username} PUT hit with username {} and user {}", username, user);
      result = userFacade.login(username, user.getPassword());
    } catch (RuntimeException e) {
      LOGGER.error("Error in /users/login/{username} PUT {}", e);
      throw new ServerException(e);
    }
    return result;
  }

  /**
   * Logs out the user specified.
   * If successful (Status Code 200).
   * If user doesn't exist (Status Code 404).
   * If cookie is invalid (Status Cod 403).
   *
   * @param username non-null String with the name of user to log out.
   * @throws UserException when user cookie is not valid or user is not found.
   * @throws ServerException when an unexpected error occurs.
   */
  @RequestMapping(value="logout/{username}", method=RequestMethod.PUT)
  void logout(@PathVariable String username,
              @RequestParam(USER_COOKIE) String cookie) throws UserException, ServerException {
    try {
      LOGGER.info("/users/logout/{username} PUT hit with username {} and cookie {}", username, cookie);
      userFacade.logout(username, cookie);
    } catch (RuntimeException e) {
      LOGGER.error("Error in /users/logout/{username} PUT {}", e);
      throw new ServerException(e);
    }
  }
}
