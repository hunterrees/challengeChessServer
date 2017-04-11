package model;

public class Move {

  private int id;
  private int gameId;
  private String startLocation;
  private String endLocation;
  private String result;

  public Move() {}

  public Move(String startLocation, String endLocation, String result) {
    this.startLocation = startLocation;
    this.endLocation = endLocation;
    this.result = result;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Move move = (Move) o;

    return id == move.id &&
            gameId == move.gameId &&
            startLocation.equals(move.startLocation) &&
            (endLocation.equals(move.endLocation) &&
                    result.equals(move.result));
  }
}
