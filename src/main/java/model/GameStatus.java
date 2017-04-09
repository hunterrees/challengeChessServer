package model;

public enum GameStatus {
  PLAYING, PLAYER1_WIN, PLAYER2_WIN, DRAW;

  public static GameStatus convert(String input) {
    switch (input) {
      case "Draw": return DRAW;
      case "Player 1 Win": return PLAYER1_WIN;
      case "Player 2 Win": return PLAYER2_WIN;
      default: return PLAYING;
    }
  }
}
