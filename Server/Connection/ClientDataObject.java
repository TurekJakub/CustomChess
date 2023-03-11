package org.connection;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
/*
*
* Data class used for representing user data in database for ORM alternative of MongoDB
*
* */
@Entity("auth_user")
public class ClientDataObject {
    @Id
    String username;
    String password;
    String email;
    boolean active;

    public ClientDataObject(String username, String password, String email, boolean active) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.active = active;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ClientDataObject() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }
}
