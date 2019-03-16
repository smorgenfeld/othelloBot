package edu.caltech.cs2.project08.bots;

import edu.caltech.cs2.project08.board.ArrayBoard;
import edu.caltech.cs2.project08.datastructures.ArrayDeque;
import edu.caltech.cs2.project08.datastructures.LinkedDeque;
import edu.caltech.cs2.project08.game.Board;
import edu.caltech.cs2.project08.game.Evaluator;
import edu.caltech.cs2.project08.game.Move;
import edu.caltech.cs2.project08.interfaces.IDeque;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;


public class AlphaBetaSearcher<B extends Board> extends AbstractSearcher<B> {
    private static final float PERCENTAGE_SEQUENTIAL = 0.5f;
    private static final int INFINITY = Integer.MAX_VALUE-1;
    @Override
    public Move getBestMove(B board, int myTime, int opTime) {
        /**
        BestMove best = alphaBeta(this.evaluator, board, ply);
        System.out.println(best.move);
        return best.move;
         **/
        //return best.move;
        //System.out.println("in");
        //if (board.isGameOver()) {
        //    return null;
        //}
        //Move k = monteCarloTree(board, 10000);
        //System.out.println(k);
        return alphaBeta(evaluator, board, ply, new BestMove(new Move(Move.PASS),-INFINITY), INFINITY).move;
    }
    private static <B extends Board> BestMove alphaBeta(Evaluator<B> evaluator, B board, int curDepth, BestMove a, int b) {
        if (curDepth == 0 || board.isGameOver()) {
            return new BestMove(a.move, evaluator.eval(board));
        }
        IDeque<Move> moves = board.getMoves();
        BestMove value;

        for (Move child : moves) {
            a.move = child;
            board.makeMove(child);
            value = new BestMove(child, -alphaBeta(evaluator, board, curDepth - 1, new BestMove(child, -b), -a.score).score);
            board.undoMove();
            if (value.score > a.score) {
                a=value;
            }
            if (a.score >= b) {
                return a;
            }
        }
        return a;
    }


/**
    private static <B extends Board> BestMove alphaBeta(Evaluator<B> evaluator, B board, int depth) {
        BestMove toReturn = new BestMove(board.getMoves().peek(), -INFINITY);
        int curScore=1;
        for (Move move : board.getMoves()) {
            board.makeMove(move);
            //curScore = prune(evaluator, board, depth-1, -INFINITY, INFINITY);
            board.undoMove();
            if (curScore>toReturn.score) {
                toReturn=new BestMove(move, curScore);
            }
        }
        return toReturn;
    }

    private <B extends Board> Move monteCarloTree(B board, int depth) {
        //System.out.println("in");


        int side = (board.isBlackMove() ? -1 : 1);
        Tree<moveBoi> tree = new Tree(new moveBoi(null));
        IDeque<Move> childs = board.getMoves();
        if (childs.size()==1) {
            return childs.removeFront();
        }
        for (Move child : childs) {
            tree.root.addChild(new moveBoi(child));
            //System.out.println("f");
        }
        Node par;
        Node searching;
        int dNum = 0;
        float best;
        float cur;
        for (int i = 0; i < depth; i++) {
            //System.out.println("whole");
            //selection
            ArrayBoard og = new ArrayBoard();
            og.setBoard((ArrayBoard)board);
            par = tree.root;
            searching = (Node)par.children.get(0);

            while (par.children.size()!=0) {
                //System.out.println("f");
                best = -999999;
                for (Node<moveBoi> child : (List<Node<moveBoi>>)par.children) {

                    cur = child.data.ucb(((moveBoi)par.data).n);
                    if (cur > 99999999) {
                        searching = child;
                        break;
                    }
                    else if (cur > best) {
                        best = cur;
                        searching = child;
                    }
                    //System.out.println("1");
                }
                par = searching;
            }
            searching = par;

            //Expansion
            //int d = 0;
            //boolean skipSimulation = false;
            IDeque<Move> list = new LinkedDeque<>();
            Node s = searching;
            while (s.parent != null) {
                list.addFront(((moveBoi)s.data).move);
                s = s.parent;
                //System.out.println("2");
            }
            while(list.size()>0) {
                //d++;
                og.makeMove(list.removeFront());
                //System.out.println("3");
            }
            //create child nodes if needed
            if (((moveBoi)searching.data).n == 0) {
                for (Move child : og.getMoves()) {
                    searching.addChild(new moveBoi(child));
                    dNum++;
                }

                searching=(Node)searching.children.get(0);
            }

            //simulation/rollout
            int size;
            IDeque<Move> moves;
            String lastMove="";
            while (!og.isGameOver()) {
                moves = og.getMoves();
                if (lastMove.equals("PASS") && moves.size()==1) {
                    break;
                }
                size = ThreadLocalRandom.current().nextInt(0, moves.size());
                //System.out.println(size);
                for (int j = 0; j < size; j++) {
                    moves.removeBack();
                }
                Move toMove = moves.removeBack();
                og.makeMove(toMove);
                lastMove = toMove.toString();
                //d++;
                //System.out.println(d + "f");
            }

            int score = (og.getScoreVanilla()*-1*side>0 ? 1 : 0);

            //System.out.println(og.getScoreVanilla()*-1*side);/

            //System.out.println((og.getScoreVanilla()*-1*side>0 ? 1 : 0)+" hh");
            //((moveBoi)searching.data).t += score;


            //back propagation
            s = searching;
            while (s != null) {
                ((moveBoi)s.data).t += score;
                ((moveBoi)s.data).n++;
                s = s.parent;
                //System.out.println("prop");
            }
        }
        float bestScore = -100000000;
        Move bestMove = tree.root.children.get(0).data.move;
        for (Node<moveBoi> child : tree.root.children) {
            //System.out.println(child.data.n + " " + child.data.t);
            if (child.data.n>0&&child.data.t/child.data.n > bestScore) {
                bestScore = child.data.t/child.data.n;
                bestMove = child.data.move;
            }
            //System.out.println("g");
        }
        System.out.println(bestScore + " " + tree.root.children.size() + " "+dNum);


        return bestMove;
    }


    public class Tree<T> {
        public Node<T> root;

        public Tree(T rootData) {
            root = new Node<T>(rootData, null);

        }


    }
    public class Node<T> {
        public T data;
        public Node<T> parent;
        public List<Node<T>> children;
        public Node (T data, Node parent) {
            this.parent = parent;
            this.data = data;
            this.children = new ArrayList<Node<T>>();

        }

        public void addChild(T child) {
            this.children.add(new Node(child, this));
        }
    }
    public class moveBoi {
        public Move move;
        public float t;
        public int n;

        public moveBoi(Move toAdd) {
            this.move = toAdd;
            this.t = 0;
            this.n = 0;
        }
        public float ucb(int N) {
            if (n == 0 || N == 0) {
                return 999999999;
            }
            return (float)(t/n+2*Math.sqrt(Math.log(N)/n));
        }
    }
    **/
}