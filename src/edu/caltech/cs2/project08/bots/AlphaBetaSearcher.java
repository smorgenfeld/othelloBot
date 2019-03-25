package edu.caltech.cs2.project08.bots;

import edu.caltech.cs2.project08.board.ArrayBoard;
import edu.caltech.cs2.project08.datastructures.ArrayDeque;
import edu.caltech.cs2.project08.datastructures.LinkedDeque;
import edu.caltech.cs2.project08.game.Board;
import edu.caltech.cs2.project08.game.Evaluator;
import edu.caltech.cs2.project08.game.Move;
import edu.caltech.cs2.project08.interfaces.IDeque;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class AlphaBetaSearcher<B extends Board> extends AbstractSearcher<B> {
    private static final float PERCENTAGE_SEQUENTIAL = 0.5f;
    private static final int INFINITY = Integer.MAX_VALUE-1;
    @Override
    public Move getBestMove(B board, int myTime, int opTime) {
        //return alphaBeta(evaluator, board, ply, new BestMove(new Move(Move.PASS),-INFINITY), INFINITY).move;

        int depth = 20000;
        int processors = 4;//Runtime.getRuntime().availableProcessors() - 1;
        Thread[] threads = new Thread[processors];
        for (int i = 0; i < processors; i++) {
            threads[i] = new Task(depth, (ArrayBoard) board);
            threads[i].run();
        }
        for (Thread t : threads) {
            try {
                t.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        float bestScore = -100000000;
        Move bestMove = ((Task)threads[0]).tree.root.children.get(0).data.move;
        int curT;
        int curN;
        for (int i = 0; i < ((Task)threads[0]).tree.root.children.size(); i++) {
            curT = 0;
            curN = 0;
            for (Thread t : threads) {
                curN += ((Task) t).tree.root.children.get(i).data.n;
                curT += ((Task) t).tree.root.children.get(i).data.t;
            }
            if (curT > 0 && curT/((float)curN) > bestScore) {
                bestScore = curT/((float)curN);
                bestMove = ((Task)threads[0]).tree.root.children.get(i).data.move;
            }
        }

        //Tree tree = monteCarloTree(board, 500);
        System.out.println(bestScore);
        return bestMove;
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


    private <B extends Board> Tree monteCarloTree(B board, int depth) {
        int side = (board.isBlackMove() ? -1 : 1);
        Tree tree = new Tree(new moveBoi((ArrayBoard)board));
        IDeque<Move> childs = board.getMoves();

        //if only one possible move just return that move
        if (childs.size()==1) {
            tree.root.addChild(new moveBoi(childs.peek(), (ArrayBoard) board));
            return tree;
        }
        Node par;
        Node searching;
        float best;
        float cur;
        ArrayBoard og = new ArrayBoard();
        for (int i = 0; i < depth; i++) {

            //selection
            par = tree.root;
            searching = null;
            if (par.children.size()!= 0) {
                searching = par.children.get(0);
            }

            while (par.children.size()!=0) {
                //System.out.println("f");
                best = -999999;
                for (Node child : par.children) {

                    cur = child.data.ucb((par.data).n);
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
            //create child nodes if needed
            if (searching.data.n == 0) {
                for (Move child : searching.data.board.getMoves()) {
                    searching.addChild(new moveBoi(child, searching.data.board));
                }

                searching = searching.children.get(0);
            }

            //simulation/rollout
            ArrayList<Move> moves;
            og.setBoard(searching.data.board);
            int count = og.getNumBlack()+og.getNumWhite();
            while (!og.isGameOver()) {
                if (count > 65) {
                    break;
                }
                moves = og.getMovesRand();
                og.makeMove(moves.get(ThreadLocalRandom.current().nextInt(0, moves.size())));
                count++;
            }

            int score = (og.getScore()*-1*side>0 ? 1 : 0);

            //back propagation
            Node s = searching;
            while (s != null) {
                s.data.t += score;
                s.data.n++;
                s = s.parent;
                //System.out.println("prop");
            }
            //System.out.println("-------------------");
        }



        return tree;
    }

    public class Task extends Thread {
        private int depth;
        private ArrayBoard curBoard;
        public Tree tree;
        public Task(int newDepth, ArrayBoard newBoard) {
            this.depth = newDepth;
            this.curBoard = newBoard;
        }

        public void run() {
            this.tree = monteCarloTree(this.curBoard, this.depth);

        }
    }


    public class Tree {
        public Node root;

        public Tree(moveBoi rootData) {
            root = new Node(rootData, null);

        }


    }
    public class Node {
        public moveBoi data;
        public Node parent;
        public List<Node> children;
        public Node (moveBoi data, Node parent) {
            this.parent = parent;
            this.data = data;
            this.children = new ArrayList<Node>();

        }

        public void addChild(moveBoi child) {
            this.children.add(new Node(child, this));
        }
    }
    public class moveBoi {
        private static final float c = 1.414f;
        public Move move;
        public float t;
        public int n;
        public ArrayBoard board;

        public moveBoi(Move toAdd, ArrayBoard newboard) {
            this.move = toAdd;
            this.board = new ArrayBoard();
            this.board.setBoard(newboard);
            this.board.makeMove(this.move);
            this.t = 0;
            this.n = 0;
        }
        public moveBoi(ArrayBoard newboard) {
            this.move = null;
            this.board = new ArrayBoard();
            this.board.setBoard(newboard);
            this.t = 0;
            this.n = 0;
        }
        public float ucb(int N) {
            if (n == 0 || N == 0) {
                return 999999999;
            }
            return (float)(t/n+c*Math.sqrt(Math.log(N)/n));
        }
    }

}