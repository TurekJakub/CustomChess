package org.connection;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

public class PasswordHasher {
    public static void main(String[] args) throws UnsupportedEncodingException {
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        try {
            gen.init("123Heslo".getBytes("UTF-8"), "rGNJTzTfz0UubTZjmXkUqi".getBytes(), 390000);
        } catch (UnsupportedEncodingException ex) {
          //  Logger.getLogger(Hasher.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] dk = ((KeyParameter) gen.generateDerivedParameters(256)).getKey();

        byte[] hashBase64 = Base64.encodeBase64(dk);
        System.out.println(new String(hashBase64));
        System.out.println(new String(hashBase64).compareTo("XIuLKyLU8r+PazV6h/iPJanT6BM7VlV66Not5IeLtrY="));
    }
    public String getPasswordHash(String password,String salt,int numberOfIterations){
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        byte[] saltt =new byte[20];
                new SecureRandom().nextBytes(saltt);
        try {
            gen.init(password.getBytes("UTF-8"), salt.getBytes(), numberOfIterations);
        } catch (UnsupportedEncodingException ex) {
            //  Logger.getLogger(Hasher.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] dk = ((KeyParameter) gen.generateDerivedParameters(256)).getKey();

        byte[] hashBase64 = Base64.encodeBase64(dk);
        System.out.println(Arrays.equals(Base64.encodeBase64(saltt),new String(Base64.encodeBase64(saltt)).getBytes()));
      return new String(hashBase64);
       // System.out.println(new String(hashBase64).compareTo("XIuLKyLU8r+PazV6h/iPJanT6BM7VlV66Not5IeLtrY="));
    }
    public String getHashedPasswordString(){
        return null;
    }
}
