package edu.caltech.cs2.project08.game;

import edu.caltech.cs2.project08.board.ArrayBoard;
import edu.caltech.cs2.project08.interfaces.IDeque;

import java.util.ArrayList;

public interface Board {
    // Stuff for Board string parsing.
    char BLACK_CHAR = '*';
    char WHITE_CHAR = 'O';
    char EMPTY_CHAR = '-';

    String MOVE_STRING_REGEX = "[" + BLACK_CHAR + WHITE_CHAR + EMPTY_CHAR + "]{64} [" + BLACK_CHAR + WHITE_CHAR + "]";
    String START_POS = "---------------------------O*------*O--------------------------- *";

    /**
     * Make the given move from the current position. Does not necessarily check if the move is legal.
     * @param m move to make
     */
    void makeMove(Move m);

    /**
     * Undo the last move that was made.
     */
    void undoMove();

    /**
     * Gets the set of moves that can be made in the position.
     * @return all legal moves
     */
    IDeque<Move> getMoves();

    /**
     * @return true if the edu.caltech.cs2.game is over.
     */
    boolean isGameOver();

    /**
     * @return true if it is black's turn.
     */
    boolean isBlackMove();

    /**
     * Returns the current edu.caltech.cs2.game score, equal to the disk parity. Positive if black is winning, negative otherwise.
     * @return current edu.caltech.cs2.game score
     */
    int getScore();

    /**
     * @return number of black disks
     */
    int getNumBlack();

    /**
     * @return number of white disks
     */
    int getNumWhite();

    // Overrides
    String toString();
    int hashCode();

    void setBoard(ArrayBoard toset);
    ArrayList<Move> getMovesRand();
}
