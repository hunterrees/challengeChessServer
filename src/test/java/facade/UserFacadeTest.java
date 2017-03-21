package facade;


import dao.UserDao;

import exception.user.InvalidPasswordException;
import exception.user.InvalidUserCookieException;
import exception.user.UserException;
import exception.user.UserNotFoundException;
import model.User;
import model.UserInfo;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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

    @Mock
    private UserDao mockUserDAO;

    @Mock
    private CookieManager mockCookieManager;

    @BeforeMethod
    public void setUp() throws UserException {
        MockitoAnnotations.initMocks(this);
        users = new ArrayList<>();
        user1 = new User("user1", "password1", "email1");
        user2 = new User("user2", "password2", "email2");
        user3 = new User("user3", "password3", "email3");

        users.add(user1);
        users.add(user2);
        users.add(user3);

        when(mockUserDAO.getAllUsers()).thenReturn(users);
        when(mockUserDAO.getUser("user1")).thenReturn(user1);
        when(mockUserDAO.getUser("user2")).thenReturn(user2);


        testUserFacade = new UserFacade(mockUserDAO);
        mockCookieManager = new CookieManager(mockUserDAO);
        doThrow(new UserNotFoundException("User Not Found")).when(mockUserDAO).getUser("badUser");


        when(mockCookieManager.makeUserCookie("user1")).thenReturn("GOODCOOKIE");
        when(mockCookieManager.validateUserCookie("GOODCOOKIE")).thenReturn(true);
        doThrow(new InvalidUserCookieException("Invalid User Cookie")).when(mockCookieManager).validateUserCookie("BADCOOKIE");


        testUserFacade = new UserFacade(mockUserDAO, mockCookieManager);

    }

    @Test
    public void getAllUsers(){
        testUserFacade.getAllUsers();
        assertEquals(testUserFacade.getAllUsers(), new ArrayList<>(Arrays.asList("user1", "user2", "user3")));
    }

    @Test
    public void getGoodUser() throws UserNotFoundException, InvalidUserCookieException {
        String user1Cookie = "GOODCOOKIE";
        UserInfo userInfo = testUserFacade.getUser("user1", user1Cookie);
        assertEquals(userInfo, new UserInfo("user1", "email1"));
    }

    @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Not Found.*")
    public void getBadUserGoodCookie() throws InvalidUserCookieException, UserNotFoundException {
        UserInfo userInfo = testUserFacade.getUser("badUser", "GOODCOOKIE");
    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void getGoodUserBadCookie() throws InvalidUserCookieException, UserNotFoundException {
        UserInfo userInfo = testUserFacade.getUser("user1", "BADCOOKIE");
    }



    @Test
    public void shouldLogin() throws UserNotFoundException, InvalidPasswordException, NoSuchAlgorithmException, InvalidUserCookieException {

        user1.setOffline();
        testUserFacade.login("user1","password1");
        assertTrue(user1.isOnline());
    }

    @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Not Found.*")
    public void noSuchUserLogin() throws UserNotFoundException, NoSuchAlgorithmException, InvalidPasswordException {
        doThrow(new UserNotFoundException("User Not Found")).when(mockUserDAO).getUser("fakeUser");
        testUserFacade.login("fakeUser","fakePassword");

    }

    @Test (expectedExceptions = InvalidPasswordException.class, expectedExceptionsMessageRegExp = ".*Invalid Password.*")
    public void badPasswordLogin() throws UserNotFoundException, NoSuchAlgorithmException, InvalidPasswordException {

        testUserFacade.login("user1","notRealPassword");

    }

}
