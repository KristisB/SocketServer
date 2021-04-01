package model;

public class MsgData {
    public MsgType msgType;
    public MsgHeader msgHeader;
    public MsgBody msgBody;

    public MsgData(MsgType msgType, MsgHeader msgHeader, MsgBody msgBody) {
        this.msgType = msgType;
        this.msgHeader = msgHeader;
        this.msgBody = msgBody;
    }


    public MsgData(MsgType msgType, MsgHeader msgHeader) {
        this.msgType =msgType;
        this.msgHeader=msgHeader;
    }
}
