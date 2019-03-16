package edu.caltech.cs2.project08.bots;

import edu.caltech.cs2.project08.game.Board;
import edu.caltech.cs2.project08.game.Evaluator;
import edu.caltech.cs2.project08.game.Move;

public class MinimaxSearcher<B extends Board> extends AbstractSearcher<B> {
    private static final int INFINITY = 999999;
    @Override
    public Move getBestMove(B board, int myTime, int opTime) {
        BestMove best = minimax(this.evaluator, board, ply);
        return best.move;
    }

    private static <B extends Board> BestMove minimax(Evaluator<B> evaluator, B board, int depth) {
        BestMove toReturn = new BestMove(board.getMoves().peek(), -INFINITY);
        int curScore;
        for (Move move : board.getMoves()) {
            board.makeMove(move);
            curScore = recurse(evaluator, board,depth-1);
            board.undoMove();
            if (curScore>=toReturn.score) {
                toReturn=new BestMove(move, curScore);
            }
        }
        return toReturn;
    }

    private static <B extends Board> int recurse(Evaluator<B> evaluator, B board, int depth) {
        if (board.isGameOver() || depth == 0) {
            return evaluator.eval(board);
        }
        int bestValue = -INFINITY;
        for (Move move : board.getMoves()) {
            board.makeMove(move);
            int value = -recurse(evaluator, board, depth-1);
            board.undoMove();
            if (value > bestValue) {
                bestValue = value;
            }
        }
        return bestValue;
    }
}