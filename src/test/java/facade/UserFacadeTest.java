package facade;


import dao.UserDao;
import exception.user.InvalidPasswordException;
import exception.user.InvalidUserCookieException;
import exception.user.UserException;
import exception.user.UserNotFoundException;
import manager.CookieManager;
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

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class UserFacadeTest {

    private UserFacade testUserFacade;

    private List<User> users;
    private User user1;
    private User user2;
    private User user3;
    private User newUser;
    private User oldUser;
    private User incompleteUser;

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
        newUser = new User("newUser", "newPass", "newEmail");
        oldUser = new User("oldUser", "oldPass", "oldEmail");
        incompleteUser = new User(null, null, null);
        users.add(user1);
        users.add(user2);
        users.add(user3);

        when(mockUserDAO.getAllUsers()).thenReturn(users);
        when(mockUserDAO.getUser("user1")).thenReturn(user1);
        when(mockUserDAO.getUser("user2")).thenReturn(user2);

        testUserFacade = new UserFacade(mockUserDAO, mockCookieManager);
        doThrow(new UserNotFoundException("User Not Found")).when(mockUserDAO).getUser("badUser");
        when(mockUserDAO.hasUser("newUser")).thenReturn(false);
        when(mockUserDAO.hasUser("oldUser")).thenReturn(true);

        when(mockCookieManager.makeUserCookie("user1")).thenReturn("GOODCOOKIE");
        when(mockCookieManager.validateUserCookie("GOODCOOKIE")).thenReturn(true);
        doThrow(new InvalidUserCookieException("Invalid User Cookie")).when(mockCookieManager).validateUserCookie("BADCOOKIE");

        when(mockCookieManager.makeUserCookie("newUser")).thenReturn("GOODCOOKIE");

        testUserFacade = new UserFacade(mockUserDAO, mockCookieManager);
    }

    //getAllUsers

    @Test
    public void getAllUsers(){
        testUserFacade.getAllUsers();
        assertEquals(testUserFacade.getAllUsers(), new ArrayList<>(Arrays.asList("user1", "user2", "user3")));
    }

    //getUser

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



    //login

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


    //logout


    @Test
    public void shouldLogout() throws InvalidUserCookieException, UserNotFoundException {
        user1.setOnline();
        testUserFacade.logout("user1", "GOODCOOKIE");
        assertTrue(!user1.isOnline());

    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void badCookieLogout() throws UserNotFoundException, NoSuchAlgorithmException, InvalidPasswordException, InvalidUserCookieException {
        user1.setOnline();
        testUserFacade.logout("user1","BADCOOKIE");
    }

    @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*User Not Found.*")
    public void badUserLogout() throws InvalidUserCookieException, UserNotFoundException {
        user1.setOnline();
        testUserFacade.logout("badUser", "GOODCOOKIE");
    }


    //register

    @Test
    public void registerNewUser() throws UserException {
        String cookie = testUserFacade.register(newUser);
        verify(mockUserDAO).addUser(newUser);
        assertEquals(cookie, "GOODCOOKIE");
    }

    @Test (expectedExceptions = UserException.class, expectedExceptionsMessageRegExp = ".*User already exists.*")
    public void registerOldUser() throws UserException {
        testUserFacade.register(oldUser);
        verify(mockUserDAO, never()).addUser(oldUser);
    }

    @Test (expectedExceptions = UserException.class, expectedExceptionsMessageRegExp = ".*User info not initialized.*")
    public void registerIncompleteUser() throws UserException {
        testUserFacade.register(incompleteUser);
        verify(mockUserDAO, never()).addUser(incompleteUser);
    }

    @Test (expectedExceptions = UserException.class, expectedExceptionsMessageRegExp = ".*: not allowed in username.*")
    public void registerInvalidUsername() throws UserException {
        testUserFacade.register(new User("has:colon", "pass", "email"));
        verify(mockUserDAO, never()).addUser(oldUser);
    }


    //update

    @Test
    public void updateGoodUser() throws InvalidUserCookieException, UserNotFoundException {
        testUserFacade.updateUser(user2, "user2:GOODCOOKIE");
        verify(mockUserDAO).updateUser(user2);
    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Invalid User Cookie.*")
    public void updateUserBadCookie() throws InvalidUserCookieException, UserNotFoundException {
        testUserFacade.updateUser(user1, "BADCOOKIE");
        verify(mockUserDAO, never()).updateUser(user1);
    }

    @Test (expectedExceptions = InvalidUserCookieException.class, expectedExceptionsMessageRegExp = ".*Cookie does not match user to update.*")
    public void updateUserNonMatchingCookie() throws InvalidUserCookieException, UserNotFoundException {
        when(mockCookieManager.validateUserCookie("user2:GOODCOOKIE")).thenReturn(true);
        testUserFacade.updateUser(user1, "user2:GOODCOOKIE");
        verify(mockUserDAO, never()).updateUser(user1);
    }



}
