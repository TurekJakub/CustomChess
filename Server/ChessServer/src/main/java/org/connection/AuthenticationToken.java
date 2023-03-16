package org.connection;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

/*
   Data class representing token used for Authentication of user during email account activation and password reset process
*/
@Entity("token")
public class AuthenticationToken {
    @Id
    private String _id = new ObjectId().toString();
    private long expiration;
    private String tokenHash;

    public AuthenticationToken(String tokenHash) {
        this.tokenHash = tokenHash;
        this.expiration = System.currentTimeMillis() + 86400000;
    }

    public AuthenticationToken() {
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
}
