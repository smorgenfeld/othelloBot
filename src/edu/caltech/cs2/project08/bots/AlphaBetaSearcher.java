package edu.caltech.cs2.project08.bots;

import edu.caltech.cs2.project08.game.Board;
import edu.caltech.cs2.project08.game.Evaluator;
import edu.caltech.cs2.project08.game.Move;
import edu.caltech.cs2.project08.interfaces.IDeque;

public class AlphaBetaSearcher<B extends Board> extends AbstractSearcher<B> {
    private static final float PERCENTAGE_SEQUENTIAL = 0.5f;
    private static final int INFINITY = 999999999;
    @Override
    public Move getBestMove(B board, int myTime, int opTime) {
        BestMove best = alphaBeta(this.evaluator, board, ply);
        return best.move;
    }

    private static <B extends Board> BestMove alphaBeta(Evaluator<B> evaluator, B board, int depth) {

        //int cores = Runtime.getRuntime().availableProcessors();
        IDeque<Move> childs = board.getMoves();
        Move bestMove = childs.peek();
        int bestVal = -INFINITY;
        int curVal;
        for (Move child : childs) {
            curVal = prune(evaluator, board, depth, -INFINITY, INFINITY);
            if (curVal > bestVal) {
                bestVal = curVal;
                bestMove = child;
            }
        }
        return new BestMove(bestMove, bestVal);
    }

    private static <B extends Board> int prune(Evaluator<B> evaluator, B board, int curDepth, int a, int b) {
        if (curDepth == 0 || board.isGameOver()) {
            return evaluator.eval(board);
        }
        int value;
        if (board.isBlackMove()) {
            value = -INFINITY;
            for (Move child : board.getMoves()) {
                board.makeMove(child);
                value = Math.max(value, prune(evaluator, board, curDepth - 1, a, b));
                board.undoMove();
                a = Math.max(a, value);
                if (a >= b) {
                    break;
                }

            }
            return value;
        }
        else {
            value = INFINITY;
            for (Move child : board.getMoves()) {
                board.makeMove(child);
                value = Math.min(value, prune(evaluator, board, curDepth - 1, a, b));
                b = Math.min(b, value);
                board.undoMove();
                if (a >= b) {
                    break;
                }

            }
            return value;
        }
    }
}