package com.alibaba.dubbo.performance.demo.agent.protocol.model;

import java.util.HashMap;
import java.util.Map;

public class ConsumerRpcRequest {

    private Map<String, String> requests = new HashMap<>(4);

    public Map<String, String> getRequests() {
        return requests;
    }

    public void setRequests(Map<String, String> requests) {
        this.requests = requests;
    }
}
