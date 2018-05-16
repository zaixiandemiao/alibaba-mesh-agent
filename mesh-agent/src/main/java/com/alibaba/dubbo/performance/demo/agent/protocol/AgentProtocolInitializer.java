package com.alibaba.dubbo.performance.demo.agent.protocol;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.IProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

public class AgentProtocolInitializer extends ChannelInitializer {

    private IProcessor processor = null;
    private RpcClient rpcClient = null;
    public AgentProtocolInitializer(IProcessor p) {
        processor = p;
    }

    public AgentProtocolInitializer() {
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new ProtocolDecoder(ProtocolService.MAX_FRAME_LENGTH,
                ProtocolService.LENGTH_FIELD_OFFSET, ProtocolService.LENGTH_FIELD_LENGTH,
                ProtocolService.LENGTH_ADJUSTMENT, ProtocolService.INITIAL_BYTES_TO_STRIP));
        pipeline.addLast(new ProtocolEncoder());
        pipeline.addLast(new ProtocolHandler());
    }
}
