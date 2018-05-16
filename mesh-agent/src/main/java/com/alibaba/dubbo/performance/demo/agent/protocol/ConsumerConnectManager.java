package com.alibaba.dubbo.performance.demo.agent.protocol;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClientInitializer;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.ConsumerSideProcessor;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.IProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * consumer agent 连接 provider agent， netty client, 3个channel
 */
public class ConsumerConnectManager {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private Bootstrap bootstrap;

    private IProcessor processor = new ConsumerSideProcessor();

    private Channel[] channels;
    private Object[] locks;

    public ConsumerConnectManager() {}
    public ConsumerConnectManager(int channelSize) {
        System.out.println("consumer connect manager initialize");
        channels = new Channel[channelSize];
        locks = new Object[channelSize];
        for(int i=0; i<channelSize; i++) {
          locks[i] = new Object();
        }
    }

    public Channel getChannel(int index,
                              String ip, int port) throws InterruptedException {
        if(null != channels[index]) {
            return channels[index];
        }
        if(null == bootstrap) {
            synchronized (this) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }
        if (null == channels[index]) {
            synchronized (locks[index]) {
                if (null == channels[index]) {
                    channels[index] = bootstrap.connect(ip, port).sync().channel();
                }
            }
        }
        return channels[index];

    }

    public void initBootstrap() {

        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new AgentProtocolInitializer(processor));
    }
}
