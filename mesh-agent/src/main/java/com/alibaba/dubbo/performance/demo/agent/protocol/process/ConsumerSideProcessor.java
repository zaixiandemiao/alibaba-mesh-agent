package com.alibaba.dubbo.performance.demo.agent.protocol.process;

import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolMsg;

public class ConsumerSideProcessor implements IProcessor<Integer> {
    @Override
    public Integer process(ProtocolMsg msg) {
        return null;
//        String str = new String(msg.getBody());
//        return Integer.parseInt(str);
    }
}
