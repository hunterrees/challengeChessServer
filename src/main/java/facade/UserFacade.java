package facade;

import dao.UserDao;
import exception.user.InvalidUserCookieException;
import exception.user.InvalidPasswordException;
import exception.user.UserException;
import exception.user.UserNotFoundException;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);
  private UserDao userDao;

  public UserFacade(){
      userDao = new UserDao();
  }
  public UserFacade(UserDao userDao){this.userDao = userDao;}

  boolean validateUserCookie(String cookie){
      //TODO: check cookie for valid user
      String cookieUserName = null;
      String cookiePassword = null;
      User tempUser = null;
      try {
          tempUser = userDao.getUser(cookieUserName);
          if(tempUser.getPassword() != cookiePassword){
              return false;
          }
      } catch (UserNotFoundException e) {
          e.printStackTrace();
          return false;
      }
          return true;
  }

  String makeUserCookie(String username) throws UserNotFoundException, NoSuchAlgorithmException {

      User user = userDao.getUser(username);
      String password = user.getPassword();
      String email = user.getEmail();
      String toHash = username + password + email;

      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

      sha256.reset();
      sha256.update(toHash.getBytes());
      byte[] fullHash = sha256.digest();

      return new String(fullHash);
  }

  /**
   * @Pre None.
   * @return List<String> of all usernames currently registered.
   */
  public List<User> getAllUsers() {
      LOGGER.info("UserFacade.getAllUsers");
      //TODO: get all users from DAO then just get the usernames and return them

      return userDao.getAllUsers();
  }

  /**
   * Get specific user.
   *
   * @Pre User must exist.
   * @return Returns UserInfo object for specified user.
   * @throws UserNotFoundException if user is not found.
   * @throws InvalidUserCookieException if the cookie is invalid.
   */
  public User getUser(String username, String cookie) throws UserNotFoundException, InvalidUserCookieException {
      LOGGER.info("UserFacade.getUser");
      //TODO: return UserInfo
      if(!validateUserCookie(cookie)){
          throw new InvalidUserCookieException();
      }
      User user = userDao.getUser(username);


      return user;
  }

  /**
   * Checks that user does not exist already and adds it to the database in the logged in position.
   *
   * @Pre Username is unique; username, password, online, and email fields are initialized; other fields are 0/null.
   * @Post User object is in database, user cookie is created.
   * @return User cookie.
   * @throws UserException if that username is already taken or if the username has a ':' in it
   */
  public String register(User basicUser) throws UserException {

      LOGGER.info("UserFacade.register");
      try {
          userDao.getUser(basicUser.getUsername());
          //TODO: throw new UserNotFoundException(); //User was already in the database

      } catch (UserNotFoundException e) {
          basicUser.setOnline();
          userDao.addUser(basicUser);
      }
      //TODO: create user cookie
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
  public String login(String username, String password) throws UserNotFoundException, InvalidPasswordException, NoSuchAlgorithmException {

      LOGGER.info("UserFacade.login");
      User tempUser = userDao.getUser(username);
      if(tempUser.getPassword() != password){
          throw new InvalidPasswordException("Invalid Password");
      }else{
          tempUser.setOnline();
          userDao.updateUser(tempUser);
      }

      return makeUserCookie(username);

  }

  /**
   *
   * @Pre User with matching username already exists.
   * @Post Sets user to offline.
   * @throws InvalidUserCookieException if the cookie is invalid.
   */
  public void logout(String username, String cookie) throws InvalidUserCookieException, UserNotFoundException{

      LOGGER.info("UserFacade.logout");
      if(!validateUserCookie(cookie)){
          throw new InvalidUserCookieException();
      }
      User tempUser = userDao.getUser(username);
      tempUser.setOffline();
      userDao.updateUser(tempUser);
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

      LOGGER.info("UserFacade.updateUser");
      if(!validateUserCookie(user.getUsername())){
          throw new InvalidUserCookieException();
      }
      userDao.updateUser(user);
  }
}
