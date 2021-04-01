import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebSocketHandler {
    public HashMap<Integer, Session> sessionsHashMap = new HashMap<Integer, Session>();
    public HashMap<Integer, User> usersHashMap = new HashMap<>();
//    public ArrayList<Session> sessionsList = new ArrayList<>();
    private MessageHandler msgHandler = new MessageHandler();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
//        sessionsList.add(session);

        InetSocketAddress remoteAddr = session.getRemoteAddress();
        System.out.println("Session " + remoteAddr.getHostString() + " connected");
//        for (Session s : sessionsList
//        ) {
//            if (s.isOpen()) {
//                System.out.println("Session: " + s.getLocalAddress().getHostString() + " is open");
//                s.getRemote().sendString(remoteAddr.getHostString() + " connected");
//
//            }
//        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
//        InetSocketAddress remoteAddr = session.getRemoteAddress();
//        sessionsList.remove(session);

        User user = closeSessionUser(session);
        sessionsHashMap.remove(user.getUserId());
        usersHashMap.remove(user.getUserId());
        try {
            sendUsersListToAll(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        MsgData msg = new MsgData(MsgType.DISCONNECT, new MsgHeader(user));
        System.out.println("User  ID" + user.getUserId() + " disconnected  because " + reason);
//        for (Map.Entry<Integer, Session> entry : sessionsHashMap.entrySet()) {
//            Session s = entry.getValue();
//            if (s.isOpen()) {
//                try {
//                    s.getRemote().sendString(msgHandler.jsonResponse(msg));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private User closeSessionUser(Session session) {
        User closingUser = null;
        for (Map.Entry<Integer, Session> entry : sessionsHashMap.entrySet()) {
            int userId = entry.getKey();
            Session s = entry.getValue();
            if (s.equals(session)) {
//                sessionsHashMap.remove(userId);
                closingUser = usersHashMap.get(userId);
            }
        }
        return closingUser;
    }

    //todo: implement handling different type msgs:
    // 1) user registration msgs,
    // 2) user connection request msg,
    // 3) user connection accept msg,
    // 4) make move msg,
    // 5) disconnect msg
    @OnWebSocketMessage
    public void handleTextMessage(Session session, String message) throws IOException {
        System.out.println("New Text Message Received from " + session.getLocalAddress().getHostString() + ": " + message);
        Gson gson = new Gson();
        MsgData msg = gson.fromJson(message, MsgData.class);

        System.out.println("msg type is: " + msg.msgType);
        switch (msg.msgType) {
            case REGISTER:
                sendRegistrationMsg(msg, session);
                break; // msgHandler.assignUserId(message);
            case MOVE:
                sendMoveData(msg);
                break;
            case CONNECTION_REQUEST:
                sendRequestMsg(msg);
                break;
            case CONNECTION_ACCEPT:
                sendAcceptMsg(msg);
                break;
            case DISCONNECT:
                disconnect(msg);
                break;

        }
    }

    private void sendAcceptMsg(MsgData msg) throws IOException {
        Session s1 =sessionsHashMap.get(msg.msgHeader.getAddress());
        if(s1.isOpen()){
            s1.getRemote().sendString(msgHandler.jsonResponse(msg));
        }
    }

    private void sendRequestMsg(MsgData msg) throws IOException {
        Session s1 =sessionsHashMap.get(msg.msgHeader.getAddress());
        if(s1.isOpen()){
            s1.getRemote().sendString(msgHandler.jsonResponse(msg));
        }
    }

    private void sendMoveData(MsgData msg) throws IOException {
        Session s1 = sessionsHashMap.get(msg.msgHeader.getUser().getUserId());
        s1.getRemote().sendString(msgHandler.jsonResponse(msg));
//        Session s2 = sessionsHashMap.get(msg.msgHeader.getAddress());
//        s1.getRemote().sendString(msgHandler.jsonResponse(msg));
    }

    private void sendRegistrationMsg(MsgData msg, Session session) throws IOException {
        if (msg.msgHeader.getUser().getUserId() == 0) {
            msg = msgHandler.assignUserId(msg);
            System.out.println("userId assigned: " + msg.msgHeader.getUser().getUserId());
        }
        sessionsHashMap.put(msg.msgHeader.getUser().getUserId(), session);
        usersHashMap.put(msg.msgHeader.getUser().getUserId(), msg.msgHeader.getUser());
        MsgData msgData = new MsgData(MsgType.REGISTER, new MsgHeader(msg.msgHeader.getUser()));

        //sending REGISTRATION response
        if(session.isOpen()){
            session.getRemote().sendString(msgHandler.jsonResponse(msgData));
        }
        //sending updated users list
        sendUsersListToAll(msg.msgHeader.getUser());
    }


    private void disconnect(MsgData msg) {

    }

    @OnWebSocketMessage
    public void handleBinaryMessage(Session session, byte[] buffer, int offset, int length) throws IOException {
        System.out.println("New Binary Message Received");
        session.getRemote().sendBytes(ByteBuffer.wrap(buffer));
    }

    public void sendUsersListToAll(User updatingUser) throws IOException {
        ArrayList<User> usersList = new ArrayList<User> (usersHashMap.values());
        MsgBody msgBody = new MsgBody();
        msgBody.setUsersList(usersList);
        MsgData msg = new MsgData(MsgType.USERS_LIST_UPDATE,new MsgHeader(updatingUser),msgBody);
        sendToAll(msg);
    }

    private void sendToAll(MsgData msg) throws IOException {
        int count=0;
        for (Map.Entry<Integer, Session> entry : sessionsHashMap.entrySet()) {
            String message = msgHandler.jsonResponse(msg);
            Session s = entry.getValue();
            if (s.isOpen()) {
                s.getRemote().sendString(message);
                count++;
            }
        }
        System.out.println("sendToAll() executed. Msg sent to "+count +" addresses");
    }

}
