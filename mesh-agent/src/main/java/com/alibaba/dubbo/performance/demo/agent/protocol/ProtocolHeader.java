package com.alibaba.dubbo.performance.demo.agent.protocol;


public class ProtocolHeader {
    private byte msgType;
    private int requestId;
    private int len;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    @Override
    public String toString() {
        return "ProtocolHeader{" +
                "msgType=" + msgType +
                ", requestId=" + requestId +
                ", len=" + len +
                '}';
    }
}
