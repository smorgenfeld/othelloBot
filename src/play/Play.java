package play;

import edu.caltech.cs2.project08.game.Move;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Play {
    public static final String SERVER = "http://othello.countablethoughts.com/";
    private static boolean WAITING = false;

    public static JSONArray bots;
    public static List<String> games;
    public static boolean isConnected = false;
    public static Bot bot;

    public static void main(String[] args) {

        games = new ArrayList<>();
        bot = new Bot();
        try {

            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            opts.reconnectionDelay = 1000;
            opts.timeout = -1;
            Socket socket = IO.socket(SERVER + "?bot=" + Bot.BOT_NAME + "&pw=" + Bot.BOT_PASS, opts);
            socket.on(Socket.EVENT_CONNECT, (arg) -> {
                System.out.println("Connected to server.");
                isConnected = true;
            }).on(Socket.EVENT_DISCONNECT, (arg) -> {
                System.out.println("Disconnected from server.");
            }).on("bad_connect", (arg) -> {
                try {
                    System.out.println(((JSONObject) arg[1]).getString("message"));
                } catch (JSONException e) { }
                System.exit(1);
            })/*.on("gameover", (arg) -> {
                JSONObject obj = (JSONObject) arg[0];
                try {
                    String result = obj.getString("result");
                    if (result.equals("draw")) {
                        System.out.println("The game was a draw!");
                    }
                    else {
                        System.out.println(result + " won!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (WAITING) {
                    System.out.print(">> ");
                    WAITING = false;
                }
            })*/.on("failure", (arg) -> {
                JSONObject obj = (JSONObject) arg[0];
                String message = "";
                try {
                    message = (String) obj.get("message");
                } catch (JSONException e) { }
                System.out.println(message);
                if (WAITING) {
                    System.out.print(">> ");
                    WAITING = false;
                }

            }).on("sv_ping", (arg) -> {
                socket.emit("cl_pong");
            }).on("match", (arg) -> {
                JSONObject obj = (JSONObject) arg[0];
                String pos = "";
                int myTime = 0, opTime = 0;
                String gameId = "";
                try {
                    pos = obj.getString("pos");
                    myTime = obj.getInt("my_time");
                    opTime = obj.getInt("op_time");
                    gameId = obj.getString("game_id");
                } catch (JSONException e) { }

                if (!games.contains(gameId)) {
                    System.out.println("Watch the game at: " + SERVER + "watch?game=" + gameId);
                    games.add(gameId);
                }

                if (WAITING) {
                    System.out.print(">> ");
                    WAITING = false;
                }

                // Spawn a new thread so we don't timeout and disconnect.
                String finalPos = pos;
                int finalMyTime = myTime;
                int finalOpTime = opTime;
                String finalId = gameId;
                Thread thread = new Thread(() -> {
                    Move best = bot.getBestMove(finalPos, finalMyTime, finalOpTime);
                    socket.emit("move", best, finalId);
                });
                thread.start();
            }).on("info", (arg) -> {
                JSONObject obj = (JSONObject) (arg.length == 1 ? arg[0] : arg[1]);
                try {
                    bots = obj.getJSONArray("bots");
                } catch (JSONException e) { }
            });
            socket.connect();

            while (!isConnected) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { }
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Press enter to start! Commands:");
            System.out.println("\t match <bot name> -- challenge bot to a game");
            System.out.println("\t who -- list bots available to match");
            System.out.println("\t scores -- display scores of previous games");
            System.out.print(">> ");
            String line = scanner.nextLine();
            do {
                String[] parts = line.split(" ");
                if (parts[0].equals("match")) {
                    if (parts.length == 1) {
                        System.out.println("You must specify who you want to match!");
                        System.out.print(">> ");
                        continue;
                    }
                    socket.emit("challenge", parts[1]);
                    WAITING = true;
                } else if (parts[0].equals("scores")) {
                    printResults("");
                } else if (parts[0].equals("who")) {
                    printBots();
                } else if (parts[0].equals("run")) {
                    try {
                        runAlgorithm(socket, parts[1]);
                    } catch (Exception e) {
                        System.out.println("fuck!");
                    }
                } else if (parts[0].equals("quit")) {
                    socket.disconnect();
                }
                if (!WAITING) {
                    System.out.print(">> ");
                }
            } while ((line = scanner.nextLine()) != null);
            scanner.close();
        } catch (URISyntaxException e) {
            System.err.println("Error encountered with SocketIO URL, check SERVER.");
        }
    }

    public static void printBots() {
        System.out.println("Online Bots:");
        if (bots == null) {
            return;
        }

        for (int i = 0; i < bots.length(); i++) {
            try {
                JSONObject botObj = bots.getJSONObject(i);
                String name = botObj.getString("name");
                String status = botObj.getString("status");
                System.out.printf("\t%s - %s%s\n", name, status, name.equals(Bot.BOT_NAME) ? " - You" : "");
            } catch (JSONException e) { }
        }
    }

    public static String printResults(String toFind) {
        try {
            URL url = new URL(SERVER + "results?bot=" + Bot.BOT_NAME);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            if (toFind.equals("")) {
                System.out.println("Results:");
            }

            Map<String, String> scores = new TreeMap<>();

            JSONArray arr = new JSONArray(content.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject res = arr.getJSONObject(i);
                String result = res.getString("result");
                String against = res.getString("against");
                if (!scores.containsKey(against)) {
                    scores.put(against, "");
                }
                scores.put(against, scores.get(against) + result.charAt(0) + " ");
            }

            int l = 0;
            for (String k : scores.keySet()) {
                l = Math.max(l, k.length());
            }

            for (String k : scores.keySet()) {
                String result = scores.get(k);
                int len = Math.max(0, result.length() - 20);
                if (toFind.equals("")) {
                    System.out.println("    " + k + ": " + " ".repeat(l - k.length() + 1) + result.substring(len));
                }
                else if (k.equals(toFind)) {
                    System.out.println(result.substring(len) + " " + k);
                    return result.substring(len);
                }
            }
        } catch (JSONException e) {
            System.out.println("Error parsing JSON data.");
        } catch (Exception e) {
            System.out.println("Error retrieving scores.");
        }
        return null;
    }

    public static void runAlgorithm(Socket socket, String toChallenge) throws Exception {
        for (int k = 0; k < 50; k++) {
            Random rand = new Random();
            BufferedReader saveFile = new BufferedReader(new FileReader("save.txt"));
            int botNum = 5;
            botBoi[] fam = new botBoi[botNum];
            for (int i = 0; i < botNum; i++) {
                fam[i] = new botBoi();
                fam[i].bot.evaluator.preMidGameMod = Float.parseFloat(saveFile.readLine());
                fam[i].bot.evaluator.postMidGameMod = Float.parseFloat(saveFile.readLine());
                fam[i].bot.evaluator.MOBILITY_SCORE_MOD = Float.parseFloat(saveFile.readLine());
                fam[i].bot.evaluator.FRONTIER_DISK_MOD = Float.parseFloat(saveFile.readLine());
                fam[i].bot.evaluator.MIDGAME = Integer.parseInt(saveFile.readLine());
                fam[i].bot.evaluator.BOARD_SCORE_MOD = Float.parseFloat(saveFile.readLine());
                fam[i].score = Integer.parseInt(saveFile.readLine());
            }
            saveFile.close();
            //evolve
            int bestScore = fam[0].score;
            int worstScore = fam[0].score;
            botBoi bestBot = fam[0];
            for (int i = 0; i < botNum; i++) {
                if (fam[i].score > bestScore) {
                    bestScore = fam[i].score;
                    bestBot = fam[i];
                } else if (fam[i].score < worstScore) {
                    worstScore = fam[i].score;
                }
            }
            float min = -1*Math.max((int)(10-k/2),1);
            float max = 1*Math.max((int)(10-k/2),1);
            for (int i = 0; i < botNum; i++) {
                if (fam[i].score == worstScore) {
                    System.out.println(i + "worst");
                    fam[i] = new botBoi();
                    fam[i].bot.evaluator.preMidGameMod = bestBot.bot.evaluator.preMidGameMod;
                    fam[i].bot.evaluator.postMidGameMod = bestBot.bot.evaluator.postMidGameMod;
                    fam[i].bot.evaluator.MOBILITY_SCORE_MOD = bestBot.bot.evaluator.MOBILITY_SCORE_MOD;
                    fam[i].bot.evaluator.FRONTIER_DISK_MOD = bestBot.bot.evaluator.FRONTIER_DISK_MOD;
                    fam[i].bot.evaluator.MIDGAME = bestBot.bot.evaluator.MIDGAME;
                    fam[i].bot.evaluator.BOARD_SCORE_MOD = bestBot.bot.evaluator.BOARD_SCORE_MOD;
                    fam[i].score = 0;
                } else {
                    fam[i].bot.evaluator.preMidGameMod += min + rand.nextFloat() * (max - min);
                    fam[i].bot.evaluator.postMidGameMod += min + rand.nextFloat() * (max - min);
                    fam[i].bot.evaluator.MOBILITY_SCORE_MOD += min + rand.nextFloat() * (max - min);
                    fam[i].bot.evaluator.FRONTIER_DISK_MOD += min + rand.nextFloat() * (max - min);
                    fam[i].bot.evaluator.MIDGAME += rand.nextInt((int)(max) + 1) + (int)min;
                    fam[i].bot.evaluator.BOARD_SCORE_MOD += min + rand.nextFloat() * (max - min);
                    fam[i].score = 0;
                }
            }

            //run games
            for (int i = 0; i < botNum; i++) {
                bot = fam[i].bot;
                bot.lastScore = 0;
                for (int j = 0; j < 10; j++) {
                    socket.emit("challenge", toChallenge);
                    Thread.sleep(5000);

                }
                String kk = printResults(toChallenge);
                System.out.println(kk);
                fam[i].score += -(kk.length() - kk.replace("L", "").length()) + 3*(kk.length() - kk.replace("W", "").length());
            }


            FileWriter toSave = new FileWriter("save.txt");
            for (int i = 0; i < botNum; i++) {
                toSave.write(fam[i].bot.evaluator.preMidGameMod + "\n");
                toSave.write(fam[i].bot.evaluator.postMidGameMod + "\n");
                toSave.write(fam[i].bot.evaluator.MOBILITY_SCORE_MOD + "\n");
                toSave.write(fam[i].bot.evaluator.FRONTIER_DISK_MOD + "\n");
                toSave.write(fam[i].bot.evaluator.MIDGAME + "\n");
                toSave.write(fam[i].bot.evaluator.BOARD_SCORE_MOD + "\n");
                toSave.write(fam[i].score + "\n");
                System.out.println(fam[i].score);
            }
            toSave.write(k + "\n");
            toSave.close();

        }
    }

}


