package com.alibaba.dubbo.performance.demo.agent.protocol.process;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolHeader;
import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolMsg;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.ConsumerRpcRequest;
import com.alibaba.dubbo.performance.demo.agent.protocol.model.SerializationUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProviderSideProcessor implements IProcessor<ProtocolMsg> {

    private Logger logger = LoggerFactory.getLogger(ProviderSideProcessor.class);
    private RpcClient rpcClient = new RpcClient();

    public ProviderSideProcessor() {
    }

    @Override
    public ProtocolMsg process(ProtocolMsg msg) {
//        ConsumerRpcRequest data = SerializationUtil.deserialize(msg.getBody(), ConsumerRpcRequest.class);
//        Map  map = data.getRequests();
////        Map map = JSON.parseObject(invokeString, Map.class);
//        String interfaceName = (String) map.get("interfaceName");
//        String method = (String) map.get("method");
//        String parameterTypesString = (String) map.get("parameterTypesString");
//        String parameter = (String) map.get("parameter");
////        logger.info("Map -> " + map);
//        try {
//            byte[] result = rpcClient.invoke(interfaceName, method, parameterTypesString, parameter);
////            byte[] result = Bytes.intToByteArray(parameter.hashCode());
//            ProtocolHeader resultHeader = new ProtocolHeader();
//            resultHeader.setMsgType((byte) 2);
//            ProtocolMsg resultMsg = new ProtocolMsg();
//            Integer num = null;
//            try {
//                num = Integer.valueOf(new String(result).trim());
//            } catch (NumberFormatException ex ) {
//                num = 0;
//            }
//            byte[] body = Bytes.intToByteArray(num);
////            byte[] body = result;
//            resultHeader.setRequestId(msg.getProtocolHeader().getRequestId());
//            if(body != null)
//                resultHeader.setLen(body.length);
//            resultMsg.setBody(body);
//            resultMsg.setProtocolHeader(resultHeader);
//
//            return resultMsg;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }
}
