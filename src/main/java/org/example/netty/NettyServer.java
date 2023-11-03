package org.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyServer {
    @Value("${netty.server.port}")
    private int port;

    @Value("${netty.server.path}")
    private String path;

    public String getPath() {
        return path;
    }

    @Autowired
    private NioWebSocketChannelInitializer webSocketChannelInitializer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ChannelFuture channelFuture;

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(webSocketChannelInitializer);

            channelFuture = serverBootstrap.bind(port).sync();
            Channel channel = channelFuture.channel();
            log.info("socket 启动成功,port:{}", port);
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
