package com.alibaba.dubbo.performance.demo.agent.protocol.process;

import com.alibaba.dubbo.performance.demo.agent.protocol.ProtocolMsg;

public interface IProcessor<T> {

    T process(ProtocolMsg msg);
}
