package play;

import edu.caltech.cs2.project08.board.ArrayBoard;
import edu.caltech.cs2.project08.board.ArrayBoardFactory;
import edu.caltech.cs2.project08.bots.AbstractSearcher;
import edu.caltech.cs2.project08.bots.AlphaBetaSearcher;
import edu.caltech.cs2.project08.game.BoardFactory;
import edu.caltech.cs2.project08.game.Evaluator;
import edu.caltech.cs2.project08.game.Move;
import edu.caltech.cs2.project08.game.SimpleEvaluator;

public class Bot {
/**
    public static final String BOT_NAME = "garlicBread";//"barlicBread";//
    public static final String BOT_PASS = "rzAapFfpmgMR";//"qyNXvLScYdZY";//

 public static final String BOT_NAME = "garlicBread";//"barlicBread";//
 public static final String BOT_PASS = "rzAapFfpmgMR";//"qyNXvLScYdZY";//
**/

/**
     public static final String BOT_NAME = "barlicBread";//
     public static final String BOT_PASS = "qyNXvLScYdZY";//
     **/
/**

    public static final String BOT_NAME = "anybotname";//
    public static final String BOT_PASS = "dgvhLufUGQsN";//
 **/
    /**
    public static final String BOT_NAME = "botsarentgettingkickedofftheserverfastenoughwhentheycrash";//
    public static final String BOT_PASS = "aRofitOvEaUi";//
     public static final String BOT_NAME = "haha";//
     public static final String BOT_PASS = "BKbdMJRmdoBR";//
     public static final String BOT_NAME = "hahaha";//
     public static final String BOT_PASS = "wLGpsEXzfnEo";//


     public static final String BOT_NAME = "hahahahaha";//
     public static final String BOT_PASS = "cFxtCOQSJDfi";//
     public static final String BOT_NAME = "hahahahahaha";//
     public static final String BOT_PASS = "UoaYhKuvyVAz";//
     public static final String BOT_NAME = "hahahahahahaha";//
     public static final String BOT_PASS = "zBMpZFsHbJZB";//
     public static final String BOT_NAME = "hahahahahahahaha";//
     public static final String BOT_PASS = "YfYFJhmKdKjW";//
     public static final String BOT_NAME = "hahahahahahahahaha";//
     public static final String BOT_PASS = "rZzYNPdjZHSa";//
     public static final String BOT_NAME = "hahahahahahahahahaha";//
     public static final String BOT_PASS = "OLflOLUmZViN";//
     public static final String BOT_NAME = "hahahahahahahahahahaha";//
     public static final String BOT_PASS = "VfwBOdTKgZgL";//
     public static final String BOT_NAME = "hahahahahahahahahahahaha";//
     public static final String BOT_PASS = "arlVadXIDXlL";//
     public static final String BOT_NAME = "hahahahahahahahahahahahaha";//
     public static final String BOT_PASS = "EfndQNoWvHZe";//
     public static final String BOT_NAME = "hahahahahahahahahahahahahaha";//
     public static final String BOT_PASS = "qHKdRCgCgGlu";//
     public static final String BOT_NAME = "hahahahahahahahahahahahahahaha";//
     public static final String BOT_PASS = "PdpNGdWrSLkV";//


     public static final String BOT_NAME = "hahahahahahahahahahahahahahahaha";//
     public static final String BOT_PASS = "wjyDIItlpgIX";//
     public static final String BOT_NAME = "hahahahahahahahahahahahahahahahaha";//
     public static final String BOT_PASS = "HWSAASWSexna";//
     public static final String BOT_NAME = "hahahahahahahahahahahahahahahahahaha";//
     public static final String BOT_PASS = "aVQxapIjpPIG";//
     public static final String BOT_NAME = "hahahahahahahahahahahahahahahahahahaha";//
     public static final String BOT_PASS = "wBvmBPHjBXXB";//
     **/
    public static final String BOT_NAME = "hahahaha";//
    public static final String BOT_PASS = "CtCAyuNibxto";//


    public SimpleEvaluator<ArrayBoard> evaluator;
    private BoardFactory<ArrayBoard> boardFactory;
    private AbstractSearcher<ArrayBoard> searcher;

    public int lastScore = 0;

    public Bot() {
        // TODO: Set up your evaluator and searcher here.
        evaluator = new SimpleEvaluator<>();
        boardFactory = new ArrayBoardFactory();

        // Make sure you set all of the necessary attributes on your searcher!
        searcher = new AlphaBetaSearcher<>();
        searcher.setDepth(8);
        searcher.setEvaluator(evaluator);
    }

    public Move getBestMove(String position, int myTime, int opTime) {
        boardFactory.setPosition(position);
        ArrayBoard board = boardFactory.create();
        return searcher.getBestMove(board, myTime, opTime);
    }

}
