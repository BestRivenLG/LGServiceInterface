package org.example.action.netty;

import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.group.ChannelGroup;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;

public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private final ChannelGroup channels;

    public NettyServerHandler(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String receivedMessage = textWebSocketFrame.text();
        // 在这里处理接收到的WebSocket消息
        System.out.println("Received message: " + receivedMessage);
        // 可以在这里编写逻辑来向客户端发送消息
        Channel incoming = channelHandlerContext.channel();
        for (Channel channel : channels) {
            if (channel != incoming) {
                String text = "[" + incoming.remoteAddress() + "] " + receivedMessage + "\n";
                channel.writeAndFlush(new TextWebSocketFrame(text));
            } else {
                String text = "[you] " + receivedMessage + "\n";
                channel.writeAndFlush(new TextWebSocketFrame(text));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
