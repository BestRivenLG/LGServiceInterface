package org.example.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NioWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {


    @Value("${netty.server.path}")
    private String path;
    @Autowired
    NioWebSocketHandler nioWebSocketHandler;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(nioWebSocketHandler);
        pipeline.addLast(new WebSocketServerProtocolHandler(path));
//        pipeline.addLast(new NettyServerHandler(channelGroup));
    }
}
