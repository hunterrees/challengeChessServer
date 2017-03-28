package facade;

import dao.UserDao;
import exception.user.InvalidUserCookieException;
import exception.user.InvalidPasswordException;
import exception.user.UserException;
import exception.user.UserNotFoundException;

import model.User;
import model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import java.util.ArrayList;
import java.util.List;

public class UserFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserFacade.class);
    private UserDao userDao;
    private CookieManager cookieManager;

    public UserFacade(){
        userDao = UserDao.getInstance();
        cookieManager = new CookieManager();
    }
    public UserFacade(UserDao userDao){
        this.userDao = userDao;
        cookieManager = new CookieManager();
    }
    public UserFacade(UserDao userDAO, CookieManager cookieManager){
        this.userDao = userDAO;
        this.cookieManager = cookieManager;
    }

    /**
     * @Pre None.
     * @return List<String> of all usernames currently registered.
     */
    public List<String> getAllUsers() {
        LOGGER.info("UserFacade.getAllUsers");
        //TODO: get all users from DAO then just get the usernames and return them
        List<String> usernames = new ArrayList<String>();
        for(User u: userDao.getAllUsers()){
            usernames.add(u.getUsername());

        }
        return usernames;
    }

    /**
     * Get specific user.
     *
     * @Pre User must exist.
     * @return Returns UserInfo object for specified user.
     * @throws UserNotFoundException if user is not found.
     * @throws InvalidUserCookieException if the cookie is invalid.
     */
    public UserInfo getUser(String username, String cookie) throws UserNotFoundException, InvalidUserCookieException {
        LOGGER.info("UserFacade.getUser");
        //TODO: return UserInfo
        cookieManager.validateUserCookie(cookie);
        UserInfo user = new UserInfo(userDao.getUser(username));
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
        if(basicUser.getUsername() == null || basicUser.getPassword() == null || basicUser.getEmail() == null){
            throw new UserException("User info not initialized");
        }
        if(!userDao.hasUser(basicUser.getUsername())){
            basicUser.setOnline();
            userDao.addUser(basicUser);
        }else {
           throw new UserException("User already exists");
        }

        return cookieManager.makeUserCookie(basicUser.getUsername());
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

        LOGGER.info("UserFacade.login");
        User tempUser = userDao.getUser(username);
        if(tempUser.getPassword() != password){
            throw new InvalidPasswordException("Invalid Password");
        }else{
            tempUser.setOnline();
            userDao.updateUser(tempUser);
        }

        return cookieManager.makeUserCookie(username);

    }

    /**
     *
     * @Pre User with matching username already exists.
     * @Post Sets user to offline.
     * @throws InvalidUserCookieException if the cookie is invalid.
     */
    public void logout(String username, String cookie) throws InvalidUserCookieException, UserNotFoundException {

        LOGGER.info("UserFacade.logout");
        cookieManager.validateUserCookie(cookie);
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
        cookieManager.validateUserCookie(cookie);
        userDao.updateUser(user);
    }
}
