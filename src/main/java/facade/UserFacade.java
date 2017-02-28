package facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);

  /**
  *  @Pre:
  *  @return: List<User> of all users currently registered (see User class)
  */
  public List<User> getAllUsers();

  /**
  *   get specific user
  *  @Pre: user must exist
  *  @return: returns User object for specified user, UserException if user is not found
  */
  public User getUser(String username);


  /**
  *   checks that user does not exist already and adds it to the database in the logged in position
  *  @Pre: Username is unique; username, password, online, and email fields are initialized; other fields are 0/null
  *  @Post:User object is in database, user cookie is created
  *  @return: user cookie,
  */
  public void register(User basicUser);


  /**
  *
  *  @Pre: username and password are not null. Password is in specified encrypted format
  *  @Post:verifies that user exists, validates password, sets user to online, returns user cookie if it does exist,
  *  @return: user cookie if user exists and password is veriefied, UserNotFound or InvalidPasswordException if not
  */
  public void login(String username, String password);

  /**
  *
  *  @Pre: user with matching username already exists
  *  @Post:sets user to offline
  *
  */
  public void logout(String username);

  /**
  *   finds matching user in DB and replaces it with the new User info
  *  @Pre:user is non null
  *  @Post:user with matching username will be updated but win record wont be affected, throws UserNotFoundException if user does not exist
  *
  */
  public void updateUser(User user);



}
