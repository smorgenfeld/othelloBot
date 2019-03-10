package edu.caltech.cs2.project08.game;

public class SimpleEvaluator<B extends Board> implements Evaluator<B> {
    static final float BOARD_SCORE_MOD = 1;
    static final float MOBILITY_SCORE_MOD = 1;
    static final float FRONTIER_DISK_MOD = -1;
    /**
     * A simple parity edu.caltech.cs2.board evaluation relative to the side to move.
     * This works for any class which implements the edu.caltech.cs2.board interface, but
     * your evaluation method doesn't need to.
     *
     * Since the edu.caltech.cs2.board interface defines the score to be positive if black is ahead
     * and negative if white is ahead, we negate it if it isn't black's move.
     *
     * @param board edu.caltech.cs2.board to evaluate
     * @return parity score
     */
    public int eval(B board) {
        int score = (int)(board.getScore()*BOARD_SCORE_MOD + board.getMoves().size()*MOBILITY_SCORE_MOD + board.getFrontierDiskNum()*FRONTIER_DISK_MOD);
        return  score * (board.isBlackMove() ? 1 : -1);
    }
}
