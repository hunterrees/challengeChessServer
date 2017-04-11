package model;

public class Game {

  private int gameId;
  private String player1;
  private String player2;
  private GameStatus status;

  public Game() {}

  public Game(int gameId, String player1, String player2, GameStatus status) {
    this.gameId = gameId;
    this.player1 = player1;
    this.player2 = player2;
    this.status = status;
  }

  public String getPlayer1() {
    return player1;
  }

  public String getPlayer2() {
    return player2;
  }

  public GameStatus getStatus() {
    return status;
  }

  public void setStatus(GameStatus status) {
    this.status = status;
  }

  public int getGameId(){return gameId;}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Game game = (Game) o;

    return gameId == game.gameId &&
            (player1 != null ? player1.equals(game.player1) : game.player1 == null) &&
            (player2 != null ? player2.equals(game.player2) : game.player2 == null) &&
            status == game.status;
  }
}
