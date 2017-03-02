package api;

import exception.ServerException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
@RequestMapping ("users")
class UserEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);

  /**
   * Gets all users.
   * @return List<User> of all users on the server.
   */
  @RequestMapping(method = RequestMethod.GET)
  String getAllUsers() throws Exception {
    try {
      LOGGER.info("/users GET hit");
    } catch (RuntimeException e) {
      throw new ServerException(e);
    }
    return "Not yet implemented";
  }

  /**
   * Gets the user specified.
   * If the user doesn't exist, null is returned (Status Code 404).
   *
   * @param username non-null String with the name of user to retrieve.
   * @return User that was requested.
   */
  @RequestMapping(value="{username}", method=RequestMethod.GET)
  String getUserInfo(@PathVariable String username) throws Exception  {
    try {
      LOGGER.info("/users/{username} GET hit");
    } catch (RuntimeException e) {
      throw new ServerException(e);
    }
    return "Not yet implemented";
  }

  /**
   * Registers the user on the server.
   * If successful, returns the created user (Status Code 201).
   *
   * @param user non-null User with the information of the user to add to system.
   * @return User that was created.
   */
  @RequestMapping(value="{username}", method=RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  String register(@RequestBody User user) throws Exception {
    //Body contains user information
    try {
      LOGGER.info("/users/{username} POST hit");
    } catch (RuntimeException e) {
      throw new ServerException(e);
    }
    return "Not yet implemented";
  }

  /**
   * Updates the users information on the server.
   * If successful, returns the update user (Status Code 200).
   * If the user doesn't exist, null is returned (Status Code 404).
   *
   * @param user non-null User with the information of the user to update.
   * @return User that was updated.
   */
  @RequestMapping(value="{username}", method=RequestMethod.PUT)
  void updateUserInfo(@RequestBody User user) throws Exception  {
    //Header must contain valid user cookie
    try {
      LOGGER.info("/users/{username} PUT hit");
    } catch (RuntimeException e) {
      throw new ServerException(e);
    }
  }

  /**
   * Logins the user specified.
   * If successful, returns the logged-in user (Status Code 200).
   * If password don't match, returns null (Status Code 400).
   * If user doesn't exist, returns null (Status Code 404).
   *
   * @param username non-null String with the name of user to login.
   * @return User that was logged-in.
   */
  @RequestMapping(value="login/{username}", method=RequestMethod.PUT)
  String login(@PathVariable String username) throws Exception  {
    //Request Body has some sort of proof of password
    try {
      LOGGER.info("/users/login/{username} PUT hit");
    } catch (RuntimeException e) {
      throw new ServerException(e);
    }
    return "Not yet implemented";
  }

  /**
   * Logs out the user specified.
   * If successful, returns the logged-out user (Status Code 200).
   * If user doesn't exist, returns null (Status Code 404).
   *
   * @param username non-null String with the name of user to log out.
   * @return User that was logged-out.
   */
  @RequestMapping(value="logout/{username}", method=RequestMethod.PUT)
  void logout(@PathVariable String username) throws Exception {
    //Header must contain valid user cookie
    try {
      LOGGER.info("/users/logout/{username} PUT hit");
    } catch (RuntimeException e) {
      throw new ServerException(e);
    }
  }
}
