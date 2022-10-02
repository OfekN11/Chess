package Business.Boards;

import Business.Color;

/**
 * this class is for saving the status of a game that has finished
 */
public class GameScore {
    public enum ReasonOfFinish {CheckMate, Pat, GiveUp}

    public Color winner;
    public ReasonOfFinish reason;

    public GameScore(Color winner, ReasonOfFinish reason) {
        this.winner = winner;
        this.reason = reason;
    }
}
