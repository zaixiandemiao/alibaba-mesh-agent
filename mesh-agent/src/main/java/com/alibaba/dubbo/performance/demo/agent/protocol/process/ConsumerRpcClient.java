package com.alibaba.dubbo.performance.demo.agent.protocol.process;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ConnecManager;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.protocol.ConsumerConnectManager;
import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolHeader;
import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolMsg;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.ConsumerRpcFuture;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.ConsumerRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.ConsumerRpcRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.SerializationUtil;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerRpcClient {
    private Logger logger = LoggerFactory.getLogger(ConsumerRpcClient.class);
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    private ConsumerConnectManager connectManager;

    public ConsumerRpcClient(){
        this.connectManager = new ConsumerConnectManager();
    }

    public ConsumerRpcClient(int size) {
        this.connectManager = new ConsumerConnectManager(size);
    }

    public Integer invoke(int index, String ip, int port,
                         String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        Channel channel = connectManager.getChannel(index, ip, port);

        Map data = new HashMap();
        data.put("interfaceName", interfaceName);
        data.put("method", method);
        data.put("parameterTypesString", parameterTypesString);
        data.put("parameter", parameter);

        ProtocolMsg msg = new ProtocolMsg();
        ProtocolHeader header = new ProtocolHeader();
        header.setMsgType((byte) 0x01);

        ConsumerRpcRequest request = new ConsumerRpcRequest();
        request.setRequests(data);

        byte[] jsonStr = SerializationUtil.serialize(request);

//        byte[] jsonStr = JSON.toJSONString(data).getBytes();
        header.setLen(jsonStr.length);
        msg.setBody(jsonStr);

        ConsumerRpcFuture future = new ConsumerRpcFuture();
        int requestId = ConsumerRpcClient.idGenerator.getAndIncrement();
        header.setRequestId(requestId);
        msg.setProtocolHeader(header);
        ConsumerRpcRequestHolder.put(String.valueOf(requestId),
                future);

        channel.writeAndFlush(msg);

        Integer result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            //ConsumerRpcRequestHolder.remove(requestId);
        }
        return result;
    }
}
