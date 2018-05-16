package com.alibaba.dubbo.performance.demo.agent.protocol;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.*;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.ConsumerRpcClient;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.IProcessor;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.ProviderSideProcessor;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ProtocolHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(ProtocolHandler.class);

    private IProcessor processor = new ProviderSideProcessor();
    private final RpcClient rpcClient = new RpcClient();
    private final static ExecutorService workerThreadService = ProtocolExecutorHelper.newBlockingExecutorsUseCallerRun(Runtime.getRuntime().availableProcessors() * 2);


    private static final Object lock = new Object();

    public ProtocolHandler(IProcessor p) {
        processor = p;
    }

    public ProtocolHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        ProtocolMsg getMsg = (ProtocolMsg) msg;
        try {
//            System.out.println(getMsg);
//            logger.info("getmsg -> " + getMsg);
            byte msgType = getMsg.getProtocolHeader().getMsgType();
            if(1 == (int) msgType) { // consumer agent -> provider agent
                // 改成将getMsg放入消息队列，后台单一线程取出并invoke，之后发送给channel
                workerThreadService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Object result = providerProcess(getMsg);
                        if(null != result) {
                            ctx.writeAndFlush(result);
                        }
                    }
                });
            } else if (2 == (int) msgType) { // provider agent -> agent
                String requestId = String.valueOf(getMsg.getProtocolHeader().getRequestId());
//                System.out.println(getMsg);
                ConsumerRpcFuture consumerRpcFuture = ConsumerRpcRequestHolder.get(requestId);
                if (null != consumerRpcFuture) {
                    Integer result = null;
                    try {
                        byte[] body = getMsg.getBody();
                        result = Bytes.byteArrayToInt(body);
//                        result = Integer.valueOf(getMsg.getBody().trim());
                    } catch (NumberFormatException ex) {

                    }
                    consumerRpcFuture.done(result);
                }
            }

        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private ProtocolMsg providerProcess(ProtocolMsg msg) {
//        String invokeString = new String(msg.getBody());
        ConsumerRpcRequest data = SerializationUtil.deserialize(msg.getBody(), ConsumerRpcRequest.class);
        Map  map = data.getRequests();
//        Map map = JSON.parseObject(invokeString, Map.class);
        String interfaceName = (String) map.get("interfaceName");
        String method = (String) map.get("method");
        String parameterTypesString = (String) map.get("parameterTypesString");
        String parameter = (String) map.get("parameter");
//        logger.info("Map -> " + map);
        try {
            byte[] result = rpcClient.invoke(interfaceName, method, parameterTypesString, parameter);
//            byte[] result = Bytes.intToByteArray(parameter.hashCode());
            ProtocolHeader resultHeader = new ProtocolHeader();
            resultHeader.setMsgType((byte) 2);
            ProtocolMsg resultMsg = new ProtocolMsg();
            Integer num = null;
            try {
                num = Integer.valueOf(new String(result).trim());
            } catch (NumberFormatException ex ) {
                num = 0;
            }
            byte[] body = Bytes.intToByteArray(num);
//            byte[] body = result;
            resultHeader.setRequestId(msg.getProtocolHeader().getRequestId());
            if(body != null)
                resultHeader.setLen(body.length);
            resultMsg.setBody(body);
            resultMsg.setProtocolHeader(resultHeader);

            return resultMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
