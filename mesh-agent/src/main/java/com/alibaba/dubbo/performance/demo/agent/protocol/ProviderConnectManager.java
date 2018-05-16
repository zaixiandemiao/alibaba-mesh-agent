package com.alibaba.dubbo.performance.demo.agent.protocol;


import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.IProcessor;
import com.alibaba.dubbo.performance.demo.agent.protocol.process.ProviderSideProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

/**
 * provider Agent 启动 netty Server
 */
public class ProviderConnectManager {

    private IProcessor processor = null;
    private RpcClient rpcClient = null;



    public ProviderConnectManager() {
        System.out.println("provider connect manager initialize");
//        rpcClient = client;
//        processor = new ProviderSideProcessor(client);
    }

    public void bindLocal(int port){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new AgentProtocolInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            System.out.println("provider 启动 netty Server");

            f.channel().closeFuture().sync();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
