package facade;

import dao.UserDAO;

import exception.user.InvalidUserCookieException;
import exception.user.UserNotFoundException;
import model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ryan on 3/14/17.
 */
public class CookieManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieManager.class);
    private UserDAO userDao;
    public CookieManager(){
        userDao = UserDAO.getInstance();
    }
    public CookieManager(UserDAO userDao){
        this.userDao = userDao;
    }

    public String makeUserCookie(String username) throws UserNotFoundException {
        User user = userDao.getUser(username);
        String password = user.getPassword();
        String email = user.getEmail();
        String toHash = username + password + email;
        String hash = hash(toHash);
        String cookie = username + ":" + hash;
        return cookie;

    }

    public String hash(String toHash) {
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
        //TODO: check cookie for valid user
        String cookieUserName = StringUtils.substringBefore(cookie, ":");
        String tempCookie;

        tempCookie = makeUserCookie(cookieUserName);
        if(tempCookie == cookie){
            return true;
        }else {
            throw new InvalidUserCookieException("Invalid User Cookie");
        }
    }

}
