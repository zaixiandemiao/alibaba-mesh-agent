package com.alibaba.dubbo.performance.demo.agent.protocol.model;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;

import java.util.concurrent.ConcurrentHashMap;

public class ConsumerRpcRequestHolder {
    // key: requestId     value: RpcFuture
    private static ConcurrentHashMap<String,ConsumerRpcFuture> processingRpc = new ConcurrentHashMap<>(1024);

    public static void put(String requestId,ConsumerRpcFuture rpcFuture){
        processingRpc.put(requestId,rpcFuture);
    }

    public static ConsumerRpcFuture get(String requestId){
        return processingRpc.get(requestId);
    }

    public static void remove(String requestId){
        processingRpc.remove(requestId);
    }
}
