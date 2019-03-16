package edu.caltech.cs2.project08.game;

public class SimpleEvaluator<B extends Board> implements Evaluator<B> {
    public float BOARD_SCORE_MOD = -1f;
    public float MOBILITY_SCORE_MOD = 20;
    public float FRONTIER_DISK_MOD = 0f;
    public int MIDGAME = 10;
    public float midgameMod = 1;
    public float preMidGameMod = 0;
    public float postMidGameMod = 1;
    public float stabilityMod = 10;
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
        /**
        if (board.getNumBlack() + board.getNumWhite() < MIDGAME) {
            midgameMod = preMidGameMod;
        }
        else {
            midgameMod = postMidGameMod;
        }
        if (board.isGameOver()) {
            if (board.getScore() > 0) {
                return 99999;
            }
            else {
                return -99999;
            }
        }
        //int score = (int) (board.getScore() * BOARD_SCORE_MOD * midgameMod * (board.isBlackMove() ? 1 : -1)+ board.getMoves().size() * MOBILITY_SCORE_MOD + board.getFrontierDiskNum() * FRONTIER_DISK_MOD+(board.isBlackMove() ? -1 : 1)*board.getAdjScore());
        int score = (int)(board.getAdjScore() * midgameMod * BOARD_SCORE_MOD + MOBILITY_SCORE_MOD * board.getMoves().size()+FRONTIER_DISK_MOD*board.checkStableSide());//FRONTIER_DISK_MOD*board.getFrontierDiskNum()
        return score;
         **/
        return board.getScore();
    }
}
