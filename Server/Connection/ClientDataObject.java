package org.connection;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 *
 * Data class used for representing user data in database for ORM alternative of MongoDB
 *
 * */
@Entity("chess_user")
public class ClientDataObject {
    @Id
    ObjectId _id;
    private int id;
    private String username;
    private String password;
    private String email;
    private boolean is_active;
    private Date last_login;

    private List<AuthenticationToken> tokens;
    private List<GameDataObject> games;
    private ObjectId profilePicture;



    public ObjectId getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ObjectId profilePicture) {
        this.profilePicture = profilePicture;
    }

    public ClientDataObject(ObjectId profilePicture, String username, String password, String email, boolean active, List<AuthenticationToken> tokens, int id) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.is_active = active;
        this.tokens = tokens;
        this.last_login = null;//new Date(0);
        this.id = id;

        this.profilePicture = profilePicture;
        games = new ArrayList<>();
    }

    public List<ObjectId> getGames() {
        return games;
    }


    public void setGames(ObjectId game) {
        games.add(game);
    }

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<AuthenticationToken> getTokens() {
        return tokens;
    }

    public void setTokens(List<AuthenticationToken> tokens) {
        this.tokens = tokens;
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
        this.is_active = active;
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

    public void addToken(AuthenticationToken authenticationToken) {
        tokens.add(authenticationToken);
    }

    public boolean isActive() {
        return is_active;
    }
}
