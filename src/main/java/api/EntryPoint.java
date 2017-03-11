package api;

import exception.user.UserNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RestController;

import model.DHParams;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class EntryPoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntryPoint.class);


  private static final int RANDOM_NUMBER_BIT_LENGTH = 500;

  static Map<String, Socket> sockets;
  static Map<String, byte[]> sharedKeys;
  private Map<String, BigInteger> privateExponents;
  private DHParameterSpec params;

  public EntryPoint() throws NoSuchAlgorithmException {
    sockets = new HashMap<>();
    sharedKeys = new HashMap<>();
    privateExponents = new HashMap<>();

    BigInteger g = BigInteger.valueOf(5);
    BigInteger p = BigInteger.probablePrime(RANDOM_NUMBER_BIT_LENGTH, new SecureRandom());
    params = new DHParameterSpec(p, g);
  }

  @RequestMapping(value="socket/{username}", method=RequestMethod.POST)
  void setUpSocket(@PathVariable String username) throws UserNotFoundException {

  }

  /**
   * Sends initial Diffie-Hellman parameters to the user.
   * Also creates the public key that will be associated with the user.
   *
   * @param username non-null string of the user who is requesting the Diffie-Hellman key.
   * @return public Diffie-Hellman parameters.
   */
  @RequestMapping(value="crypt/init/{username}", method=RequestMethod.GET)
  DHParams sendParameters(@PathVariable String username) {
    if (privateExponents.get(username) == null) {
      BigInteger s = BigInteger.probablePrime(RANDOM_NUMBER_BIT_LENGTH, new SecureRandom());
      privateExponents.put(username, s);
    }
    BigInteger publicParam = modExp(params.getG(), privateExponents.get(username), params.getP());
    return new DHParams(params, publicParam.toByteArray());
  }

  /**
   * Creates shared key based on the public key given in the body.
   *
   * @param username non-null string of the user who is requesting Diffie-Hellman.
   * @param parameters Diffie-Hellman parameters of the user.
   * @throws UserNotFoundException if user hasn't requested the initial public parameters.
   */
  @RequestMapping(value="crypt/end/{username}", method=RequestMethod.POST)
  void setUpSharedKey(@PathVariable String username, @RequestBody DHParams parameters) throws UserNotFoundException {
    BigInteger publicParam = new BigInteger(parameters.getPublicParam());
    if (privateExponents.get(username) == null) {
      throw new UserNotFoundException("Request initial parameters first");
    }
    BigInteger sharedKey = modExp(publicParam, privateExponents.get(username), parameters.getParams().getP());
    byte[] result = new byte[16];
    for (int i = 0; i < 16; i++) {
      result[i] = sharedKey.toByteArray()[i];
    }
    sharedKeys.put(username, result);
  }

  BigInteger modExp(BigInteger x, BigInteger y, BigInteger n) {
    if (y.equals(BigInteger.valueOf(0))) {
      return BigInteger.valueOf(1);
    }

    BigInteger z = modExp(x, y.divide(BigInteger.valueOf(2)), n);
    if (y.mod(BigInteger.valueOf(2)).equals(BigInteger.valueOf(0))) {
      return z.multiply(z).mod(n);
    }
    else {
      return x.multiply((z.multiply(z)).mod(n)).mod(n);
    }
  }

  public static void main(String[] args) throws Exception {
    Object[] sources = {EntryPoint.class, UserEndpoint.class, GameEndpoint.class, MoveEndpoint.class};
    LOGGER.info("Starting server with following classes: {}", sources);
    SpringApplication.run(sources, args);
  }
}
