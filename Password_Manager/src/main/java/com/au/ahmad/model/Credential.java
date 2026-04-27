package com.au.ahmad.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* 
Credential Model
+---------------------+----------------------------------------------------------+--------------------------------------------------------------------+
|function             | input                                                    | output                                                             |
+---------------------+----------------------------------------------------------+--------------------------------------------------------------------+
|Credential           | int, int, String, String, String, boolean, LocalDateTime | Credential object with all fields initialized                      |
|Credential           | int, String, String, String, String                      | Credential object with default values for id, favourite, createdAt |
|getId                | -                                                        | int (credential ID)                                                |
|getUserId            | -                                                        | int (associated user ID)                                           |
|getSite              | -                                                        | String (site name)                                                 |
|getAccountUsername   | -                                                        | String (account username)                                          |
|getPassword          | -                                                        | String (account password)                                          |
|getPriority          | -                                                        | String (credential priority)                                       |
|isFavourite          | -                                                        | boolean (favourite status)                                         |
|getCreatedAt         | -                                                        | LocalDateTime (creation timestamp)                                 |
|getCreatedAtFormatted| -                                                        | String (formatted creation timestamp)                              |
|setId                | int                                                      | void (sets credential ID)                                          | 
|setUserId            | int                                                      | void (sets associated user ID)                                     |   
|setSite              | String                                                   | void (sets site name)                                              |
|setAccountUsername   | String                                                   | void (sets account username)                                       |
|setPassword          | String                                                   | void (sets account password)                                       |
|setPriority          | String                                                   | void (sets credential priority)                                    |
|setFavourite         | boolean                                                  | void (sets favourite status)                                       |
|setCreatedAt         | LocalDateTime                                            | void (sets creation timestamp)                                     |
|toString             | -                                                        | String (string representation of the credential)                   |
+---------------------+----------------------------------------------------------+--------------------------------------------------------------------+

*/

public class Credential {

    private int    id;
    private int    userId;
    private String site;
    private String accountUsername;
    private String password;
    private String priority;
    private boolean favourite;
    private LocalDateTime createdAt;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Credential(int id, int userId, String site,
                      String accountUsername, String password,
                      String priority, boolean favourite,
                      LocalDateTime createdAt) {
        this.id              = id;
        this.userId          = userId;
        this.site            = site;
        this.accountUsername = accountUsername;
        this.password        = password;
        this.priority        = priority;
        this.favourite       = favourite;
        this.createdAt       = createdAt;
    }

    public Credential(int userId, String site,
                      String accountUsername, String password,
                      String priority) {
        this(0, userId, site, accountUsername, password,
             priority, false, LocalDateTime.now());
    }

    public int            getId()              { return id; }
    public int            getUserId()          { return userId; }
    public String         getSite()            { return site; }
    public String         getAccountUsername() { return accountUsername; }
    public String         getPassword()        { return password; }
    public String         getPriority()        { return priority; }
    public boolean        isFavourite()        { return favourite; }
    public LocalDateTime  getCreatedAt()       { return createdAt; }

    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(FMT) : "";
    }

    public void setId(int id)                          { this.id = id; }
    public void setUserId(int userId)                  { this.userId = userId; }
    public void setSite(String site)                   { this.site = site; }
    public void setAccountUsername(String u)           { this.accountUsername = u; }
    public void setPassword(String password)           { this.password = password; }
    public void setPriority(String priority)           { this.priority = priority; }
    public void setFavourite(boolean favourite)        { this.favourite = favourite; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Credential{id=%d, userId=%d, site='%s', " +
                             "user='%s', priority='%s', favourite=%b, created='%s'}",
                             id, userId, site, accountUsername,
                             priority, favourite, getCreatedAtFormatted());
    }
}