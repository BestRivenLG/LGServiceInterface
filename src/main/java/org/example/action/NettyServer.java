package org.example.action;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
//import io.netty.channel.ChannelPipeline;

@Slf4j
@Component
public class NettyServer {
    @Value("${netty.server.port}")
    private int port;

    private final ChannelGroup channelGroup;

    public NettyServer() {
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65535));
                            pipeline.addLast(new NioWebSocketHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
//                            pipeline.addLast(new NettyServerProtocolHandler("/chat"));
                            pipeline.addLast(new NettyServerHandler(channelGroup));
                        }
                    });

            Channel channel = serverBootstrap.bind(port).sync().channel();
            log.info("socket 启动成功,port:{}", port);
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
