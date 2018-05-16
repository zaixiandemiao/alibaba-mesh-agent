package com.alibaba.dubbo.performance.demo.agent.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ProtocolDecoder extends LengthFieldBasedFrameDecoder {
    /**
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     * @param lengthAdjustment
     * @param initialBytesToStrip
     */
    public ProtocolDecoder(int maxFrameLength, int lengthFieldOffset,
                           int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
                lengthAdjustment, initialBytesToStrip);
    }

    /**
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     * @param lengthAdjustment
     * @param initialBytesToStrip
     * @param failFast
     */
    public ProtocolDecoder(int maxFrameLength, int lengthFieldOffset,
                           int lengthFieldLength, int lengthAdjustment,
                           int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength,
                lengthAdjustment, initialBytesToStrip, failFast);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected ProtocolMsg decode(ChannelHandlerContext ctx, ByteBuf in2) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, in2);
        if (in == null) {
            return null;
        }
        byte msgType = in.readByte();
        int requestId = in.readInt();
        int len = in.readInt();

        if (in.readableBytes() < len) {
            return null;
        }
        ByteBuf buf = in.readBytes(len);
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
//        String body = new String(req, "UTF-8");
        ProtocolMsg msg = new ProtocolMsg();
        ProtocolHeader header = new ProtocolHeader();
        header.setMsgType(msgType);
        header.setRequestId(requestId);
        header.setLen(len);
        msg.setBody(req);
        msg.setProtocolHeader(header);

        return msg;
    }
}
