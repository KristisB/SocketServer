import spark.Spark;

import static spark.Spark.*;

public class Server {
    public static void main(String[] args) {
        Spark.port(9000);
//        staticFileLocation("/public");
        webSocket("/socket", WebSocketHandler.class);
        init();
        System.out.println("websocket started ");


    }
}
