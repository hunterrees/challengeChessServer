package model;

import java.util.ArrayList;
import java.util.List;

public class User {

  private String username;
  private String password;
  private String email;
  private boolean online;
  private List<String> friends;
  private int rank;
  private int wins;
  private int loses;
  private int draws;

  public User(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
    online = true;
    friends = new ArrayList<>();
    rank = 0;
    wins = 0;
    loses = 0;
    draws = 0;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setOnline() {
    this.online = true;
  }

  public void setOffline() {
    this.online = false;
  }

  public void addFriend(String friend) {
    friends.add(friend);
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public void addWin() {
    wins++;
  }

  public void addLoss() {
    loses++;
  }

  public void addDraw() {
    draws++;
  }
}
