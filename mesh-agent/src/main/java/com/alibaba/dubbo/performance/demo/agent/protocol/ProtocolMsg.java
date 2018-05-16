package com.alibaba.dubbo.performance.demo.agent.protocol;

public class ProtocolMsg {


    private ProtocolHeader protocolHeader = new ProtocolHeader();
    private byte[] body;

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }


    /**
     *
     */
    public ProtocolMsg() {
        // TODO Auto-generated constructor stub
    }

    public ProtocolHeader getProtocolHeader() {
        return protocolHeader;
    }

    public void setProtocolHeader(ProtocolHeader protocolHeader) {
        this.protocolHeader = protocolHeader;
    }

    @Override
    public String toString() {
        return "ProtocolMsg{" +
                "protocolHeader=" + protocolHeader.toString() +
                ", body='" + body + '\'' +
                '}';
    }
}
