package facade;


import dao.UserDao;

import exception.user.InvalidPasswordException;
import exception.user.InvalidUserCookieException;
import exception.user.UserException;
import exception.user.UserNotFoundException;
import model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class UserFacadeTest {

    private UserFacade testUserFacade;

    private List<User> users;
    private User user1;
    private User user2;
    private User user3;

    private CookieManager cookieManager;

    @Mock
    private UserDao mockUserDao;

    @BeforeMethod
    public void setUp() throws UserException {
        MockitoAnnotations.initMocks(this);
        users = new ArrayList<>();
        user1 = new User("user1", "password1", "email1");
        user2 = new User("user2", "password2", "email2");
        user3 = new User("user3", "password3", "email3");

        users.add(user1);
        users.add(user2);


        when(mockUserDao.getAllUsers()).thenReturn(users);
        when(mockUserDao.getUser("user1")).thenReturn(user1);
        when(mockUserDao.getUser("user2")).thenReturn(user2);


<<<<<<< HEAD
        testUserFacade = new UserFacade(mockUserDao);
=======
        testUserFacade = new UserFacade(mockUserDAO);
        cookieManager = new CookieManager(mockUserDAO);
>>>>>>> cookie manager, userdao is singleton, getalluser returns list<String>, get user returns userinfo, changed some things in userAPI to match that
    }

    @Test
    public void shouldMakeMatchingHash() throws UserNotFoundException, NoSuchAlgorithmException{
        String user1Cookie = cookieManager.makeUserCookie("user1");
        String user1Cookie2 = cookieManager.makeUserCookie("user1");
        assertEquals(user1Cookie, user1Cookie2);
    }

    @Test
    public void shouldMakeNonMatchingHash() throws UserNotFoundException, NoSuchAlgorithmException{
        String user1Cookie = cookieManager.makeUserCookie("user1");
        String user2Cookie = cookieManager.makeUserCookie("user2");
        assertNotEquals(user1Cookie, user2Cookie);
    }

    @Test
    public void shouldLogin() throws UserNotFoundException, InvalidPasswordException, NoSuchAlgorithmException, InvalidUserCookieException {

        user1.setOffline();
        testUserFacade.login("user1","password1");
        assertTrue(user1.isOnline());
    }

    @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Not Found.*")
    public void noSuchUserLogin() throws UserNotFoundException, NoSuchAlgorithmException, InvalidPasswordException {
        doThrow(new UserNotFoundException("User Not Found")).when(mockUserDao).getUser("fakeUser");
        testUserFacade.login("fakeUser","fakePassword");
    }

    @Test (expectedExceptions = InvalidPasswordException.class, expectedExceptionsMessageRegExp = ".*Invalid Password.*")
    public void badPasswordLogin() throws UserNotFoundException, NoSuchAlgorithmException, InvalidPasswordException {

        testUserFacade.login("user1","notRealPassword");

    }

}
