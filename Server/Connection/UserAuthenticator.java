package org.connection;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.InputMismatchException;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

/*
    Class providing implementations of basic cryptography needed for authentication of users like password hashing,
    authentication tokens generation etc.
*/
public class UserAuthenticator {
    /*
        Return given password hashed by pbkdf2_sha256 algorithm with given base64 encoded salt and work force
    */
    // source: https://gist.github.com/lukaszb/1af1bd4233326e37a8a0?permalink_comment_id=1383413#gistcomment-1383413
    public String getPasswordHash(String password, String salt, int numberOfIterations) {
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password.getBytes(StandardCharsets.UTF_8), salt.getBytes(), numberOfIterations);
        byte[] dk = ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
        byte[] hashBase64 = Base64.encodeBase64(dk);
        return new String(hashBase64);

    }

    /*
       Generate new random 16-byte salt and return String consisting of this base64 encoded salt, algorithm work factor
       and given password hashed with pbkdf2_sha256 algorithm and generated salt
       String is in Django framework supported format
    */
    public String getHashedPasswordString(String password, int numberOfIterations) {
        String saltString = getSaltString(16);
        String passwordHashString = getPasswordHash(password, saltString, numberOfIterations);
        return "pbkdf2_sha256$" + numberOfIterations + "$" + saltString + "$" + passwordHashString;

    }

    /*
        Return salt of given size in bytes
    */
    private String getSaltString(int size) {
        byte[] salt = new byte[size];
        new SecureRandom().nextBytes(salt);
        return new String(Base64.encodeBase64(salt));
    }

    /*
      Return random URL safe String of given size in bytes, that is used as value of user AuthenticationToken
    */
    public String getAuthenticationTokenString(int size) {
        byte[] tokenValue = new byte[size];
        new SecureRandom().nextBytes(tokenValue);
        return new String(Base64.encodeBase64(tokenValue));
    }

    public String getUrlEncodedId(int id) {
        return new String(Base64.encodeBase64URLSafe(String.valueOf(id).getBytes()));
    }

    /*
      Return instance of AuthenticationToken with given value prepared for saving in database
    */
    public AuthenticationToken getAuthenticationToken(String tokenValueString, int numberOfIterations) {
        String tokenValueHash ="pbkdf2_sha256$" + numberOfIterations + "$" + "" + "$" +  getPasswordHash(tokenValueString, "", numberOfIterations);
        return new AuthenticationToken(tokenValueHash);
    }


}
