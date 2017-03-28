package manager;


import dao.UserDao;
import exception.user.InvalidUserCookieException;
import exception.user.UserException;
import exception.user.UserNotFoundException;
import manager.CookieManager;
import model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;


public class CookieManagerTest {

    private CookieManager testCookieManager;

    private List<User> users;
    private User user1;
    private User user2;
    private User user3;

    @Mock
    private UserDao mockUserDAO;

    @BeforeMethod
    public void setUp() throws UserException {
        MockitoAnnotations.initMocks(this);
        users = new ArrayList<>();
        user1 = new User("user1", "password1", "email1");
        user2 = new User("user2", "password2", "email2");
        user3 = new User("user3", "password3", "email3");

        users.add(user1);
        users.add(user2);


        when(mockUserDAO.getAllUsers()).thenReturn(users);
        when(mockUserDAO.getUser("user1")).thenReturn(user1);
        when(mockUserDAO.getUser("user2")).thenReturn(user2);


        testCookieManager = new CookieManager(mockUserDAO);
    }

    @Test
    public void shouldMakeMatchingCookie() throws UserNotFoundException {
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        String user1Cookie2 = testCookieManager.makeUserCookie("user1");
        assertEquals(user1Cookie, user1Cookie2);
    }

    @Test
    public void shouldMakeNonMatchingCookie() throws UserNotFoundException{
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        String user2Cookie = testCookieManager.makeUserCookie("user2");
        assertNotEquals(user1Cookie, user2Cookie);
    }

    @Test
    public void shouldValidateCookie() throws UserNotFoundException, InvalidUserCookieException {
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        assertTrue(testCookieManager.validateUserCookie(user1Cookie));
    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void shouldNotValidateCookieGoodUserName() throws InvalidUserCookieException, UserNotFoundException {
        String user1Cookie = testCookieManager.makeUserCookie("user1");
        user1Cookie += "badStuff";
        testCookieManager.validateUserCookie(user1Cookie);
    }

    @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Not Found.*")
    public void shouldNotValidateCookieBadUserName() throws InvalidUserCookieException, UserNotFoundException {
        doThrow(new UserNotFoundException("User Not Found")).when(mockUserDAO).getUser("user5");
        testCookieManager.validateUserCookie("user5:blahblahblah");
    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void shouldNotValidateCookieBadUserNameNoColon() throws InvalidUserCookieException, UserNotFoundException {
        testCookieManager.validateUserCookie("user5blahblahblah");
    }


}
