package com.alibaba.dubbo.performance.demo.agent.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;

public class ProtocolEncoder extends MessageToByteEncoder<ProtocolMsg> {
    /**
     *
     */
    public ProtocolEncoder() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param outboundMessageType
     */
    public ProtocolEncoder(Class<? extends ProtocolMsg> outboundMessageType) {
        super(outboundMessageType);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param preferDirect
     */
    public ProtocolEncoder(boolean preferDirect) {
        super(preferDirect);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param outboundMessageType
     * @param preferDirect
     */
    public ProtocolEncoder(Class<? extends ProtocolMsg> outboundMessageType,
                           boolean preferDirect) {
        super(outboundMessageType, preferDirect);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see io.netty.handler.codec.MessageToByteEncoder#encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, io.netty.buffer.ByteBuf)
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMsg msg,
                          ByteBuf out) throws Exception {
        if (msg == null | msg.getProtocolHeader() == null) {
            throw new Exception("The encode message is null");
        }
        ProtocolHeader header = msg.getProtocolHeader();
//        String body = msg.getBody();
//        byte[] bodyBytes = body.getBytes(Charset.forName("utf-8"));
        byte[] bodyBytes = msg.getBody();
        int bodySize = bodyBytes.length;

        out.writeByte(header.getMsgType());
        out.writeInt(header.getRequestId());
        out.writeInt(bodySize);
        out.writeBytes(bodyBytes);
    }
}

