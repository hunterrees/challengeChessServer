package api;

import org.testng.annotations.BeforeMethod;

public class EntryPointTest {

  private EntryPoint testModel;

  @BeforeMethod
  public void setUp() {
    testModel = new EntryPoint();
  }
}
