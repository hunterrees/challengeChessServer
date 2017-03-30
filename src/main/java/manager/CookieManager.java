package manager;

import dao.UserDao;
import exception.game.GameNotFoundException;
import exception.game.InvalidGameCookieException;
import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CookieManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieManager.class);
    private UserDao userDao;

    private static CookieManager instance;

    private CookieManager(){
        userDao = UserDao.getInstance();
    }

    CookieManager(UserDao userDao){
        this.userDao = userDao;
    }

    public static CookieManager getInstance() {
      if (instance == null) {
        instance = new CookieManager();
      }
      return instance;
    }

    public String makeUserCookie(String username) throws UserNotFoundException {
        User user = userDao.getUser(username);
        String password = user.getPassword();
        String email = user.getEmail();
        String toHash = username + password + email;
        String hash = hash(toHash);
        return username + ":" + hash;
    }

    private String hash(String toHash) {
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
            sha256.reset();
            sha256.update(toHash.getBytes());
            byte[] fullHash = sha256.digest();
            return new String(fullHash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("no such algorithm {}", e);
            return null;
        }
    }

    public boolean validateUserCookie(String cookie) throws UserNotFoundException, InvalidUserCookieException {
        int indexOfColon = cookie.indexOf(':');
        if(indexOfColon == -1){
            throw new InvalidUserCookieException("Invalid User Cookie");
        }
        String cookieUserName = cookie.substring(0, indexOfColon);
        String tempCookie;

        tempCookie = makeUserCookie(cookieUserName);
        if(tempCookie.equals(cookie)){
            return true;
        }else {
            throw new InvalidUserCookieException("Invalid User Cookie");
        }
    }

    public String makeGameCookie() {
      return null;
    }

    public boolean validateGameCookie(String cookie) throws GameNotFoundException,InvalidGameCookieException {
      return false;
    }
}
