import com.google.gson.Gson;
import model.MsgData;
import model.MsgType;
import model.User;

import java.util.Map;

public class MessageHandler {

    MsgType msgType;
    int userId=1;

    public MessageHandler() {

    }

    public MsgType detectMsgType(String jsonLine) {
        Gson gson = new Gson();
        MsgData msg= gson.fromJson(jsonLine,MsgData.class);
        return msg.msgType;
    }

    public MsgData assignUserId(MsgData msgData){
        User user=msgData.msgHeader.getUser();
        user.setUserId(userId);
        userId++;
        return msgData;
    }

    public String jsonResponse(MsgData msgData){
        Gson gson = new Gson();
        return gson.toJson(msgData);
    }


}


