package manager;

import exception.user.UserNotFoundException;
import model.DHParams;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class EncryptionManagerTest {

  private EncryptionManager testModel;

  @BeforeMethod
  public void setUp() {
    testModel = new EncryptionManager();
  }

  @Test
  public void shouldSendPublicParametersBack() {
    DHParams result = testModel.getPublicParameters("test1");

    assertEquals(result.getParams().getG(), BigInteger.valueOf(5));
    assertEquals(result.getParams().getP().bitLength(), 500);
  }

  @Test
  public void shouldGenerateDifferentPublicParametersForEachUser() {
    DHParams test1 = testModel.getPublicParameters("test1");
    DHParams test2 = testModel.getPublicParameters("test2");

    assertNotEquals(test1.getPublicParam(), test2.getPublicParam());
  }

  @Test
  public void shouldNotOverrideParameterForUserIfCalledAgain() {
    DHParams result = testModel.getPublicParameters("test1");
    DHParams result2 = testModel.getPublicParameters("test1");

    assertEquals(result.getPublicParam(), result2.getPublicParam());
  }

  @Test
  public void shouldGenerateSameSharedKey() throws UserNotFoundException {
    byte[] newSharedKey = generateSharedKey();

    assertEquals(Arrays.toString(newSharedKey), Arrays.toString(testModel.sharedKeys.get("test1")));
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*initial parameters.*")
  public void shouldThrowExceptionWhenSharedKeyIsAttemptedWithoutInitialization() throws UserNotFoundException {
    DHParameterSpec params = new DHParameterSpec(BigInteger.valueOf(5), BigInteger.valueOf(7));
    testModel.generateSharedKey("test2", new DHParams(params, new byte[2]));
  }

  @Test (expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = ".*does not have a shared.*")
  public void shouldThrowExceptionWhenAttemptingToDecodePasswordWithoutSharedKey() throws UserNotFoundException {
    testModel.decryptPassword("test2", "password".getBytes());
  }

  @Test
  public void shouldDecryptPasswordProperly() throws UserNotFoundException, GeneralSecurityException, UnsupportedEncodingException {
    byte[] sharedKey = generateSharedKey();

    SecretKeySpec key = new SecretKeySpec(sharedKey, EncryptionManager.ENCRYPTION_ALGORITHM);
    Cipher cipher = Cipher.getInstance(EncryptionManager.ENCRYPTION_MODE);
    cipher.init(Cipher.ENCRYPT_MODE, key, testModel.parameterSpec);
    String password = "password";
    byte[] encrypted = cipher.doFinal(password.getBytes());

    String decryptedPassword = testModel.decryptPassword("test1", encrypted);

    assertEquals(decryptedPassword, password);
  }

  private byte[] generateSharedKey() throws UserNotFoundException {
    DHParams result = testModel.getPublicParameters("test1");
    BigInteger a = BigInteger.probablePrime(500, new SecureRandom());
    BigInteger publicKey = testModel.modExp(result.getParams().getG(), a, result.getParams().getP());

    BigInteger expectedSharedKey = testModel.modExp(new BigInteger(result.getPublicParam()), a, result.getParams().getP());
    testModel.generateSharedKey("test1", new DHParams(result.getParams(), publicKey.toByteArray()));

    byte[] newSharedKey = new byte[16];
    for (int i = 0; i < 16; i++) {
      newSharedKey[i] = expectedSharedKey.toByteArray()[i];
    }
    return newSharedKey;
  }
}
