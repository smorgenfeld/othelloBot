package play;

import edu.caltech.cs2.project08.game.Move;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

public class botBoi {
    public Bot bot;
    public int score;

    public botBoi() {
        bot = new Bot();
        score = 0;
    }
}
