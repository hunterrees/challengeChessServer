package model;

import javax.crypto.spec.DHParameterSpec;

public class DHParams {

  private DHParameterSpec params;
  private byte[] publicParam;

  public DHParams(DHParameterSpec params, byte[] publicParam) {
    this.params = params;
    this.publicParam = publicParam;
  }

  public DHParameterSpec getParams() {
    return params;
  }

  public byte[] getPublicParam() {
    return publicParam;
  }

}
