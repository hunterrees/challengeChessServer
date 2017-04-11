package model;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

  private final String username;
  private String email;
  private boolean online;
  private final List<String> friends;
  private int rank;
  private int wins;
  private int loses;
  private int draws;

  public UserInfo(String username, String email) {
    this.username = username;
    this.email = email;
    online = true;
    friends = new ArrayList<>();
    rank = 0;
    wins = 0;
    loses = 0;
    draws = 0;
  }

  public UserInfo(User user) {
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.online = user.isOnline();
    this.friends = user.getFriends();
    this.rank = user.getRank();
    this.wins = user.getWins();
    this.loses = user.getLoses();
    this.draws = user.getDraws();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserInfo userInfo = (UserInfo) o;

    return online == userInfo.online &&
            rank == userInfo.rank &&
            wins == userInfo.wins &&
            loses == userInfo.loses &&
            draws == userInfo.draws &&
            (username != null ? username.equals(userInfo.username) : userInfo.username == null) &&
            (email != null ? email.equals(userInfo.email) : userInfo.email == null) &&
            (friends != null ? friends.equals(userInfo.friends) : userInfo.friends == null);
  }

}
