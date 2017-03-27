package model;

public class Move {

  private int id;
  private int gameId;
  private String startLocation;
  private String endLocation;
  private String result;

  public Move(String startLocation, String endLocation, String result) {
    this.startLocation = startLocation;
    this.endLocation = endLocation;
    this.result = result;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getGameId() {
    return gameId;
  }

  public void setGameId(int gameId) {
    this.gameId = gameId;
  }

  public String getStartLocation() {
    return startLocation;
  }

  public void setStartLocation(String startLocation) {
    this.startLocation = startLocation;
  }

  public String getEndLocation() {
    return endLocation;
  }

  public void setEndLocation(String endLocation) {
    this.endLocation = endLocation;
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
