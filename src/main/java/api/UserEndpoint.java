package api;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
@RequestMapping ("users")
class UserEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEndpoint.class);

  /**
   * Gets all users
   * @return List<User> of all users on the server
   */
  @RequestMapping(method = RequestMethod.GET)
  String getAllUsers() {
    LOGGER.info("/users GET hit");
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
  String getUserInfo(@PathVariable String username) {
    LOGGER.info("/users/{username} GET hit");
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
  String register(@RequestBody User user) {
    //Body conatins user information
    LOGGER.info("/users/{username} POST hit");
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
  String updateUserInfo(@RequestBody User user) {
    //Header must contain valid user cookie
    LOGGER.info("/users/{username} PUT hit");
    return "Not yet implemented";
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
  String login(@PathVariable String username) {
    //Request Body has some sort of proof of password
    LOGGER.info("/users/login/{username} PUT hit");
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
  String logout(@PathVariable String username) {
    //Header must contain valid user cookie
    LOGGER.info("/users/logout/{username} PUT hit");
    return "Not yet implemented";
  }
}
