package org.example.action;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

public class NettyServerProtocolHandler extends WebSocketServerProtocolHandler {

    public NettyServerProtocolHandler(String websocketPath) {
        super(websocketPath);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                String text = textFrame.text();
                System.out.println("Received message: " + text);
            }
        }

        if (msg instanceof FullHttpRequest) {
//            if (msg instanceof HttpObjectAggregator) {
            FullHttpRequest mess = (FullHttpRequest) msg;
            // 获取握手请求的URI
//            AttributeKey<String> atStrKey = AttributeKey.valueOf("message");
            String uri = mess.getUri();
            String token = getTokenFromUri(uri);
//            String token = getTokenFromUri(ctx.channel().attr(atStrKey).get());
            System.out.println("Received message: " + ", Token: " + token);
        }



        super.channelRead(ctx, msg);
    }


    private String getTokenFromUri(String uri) {
        String[] parts = uri.split("\\?");
        if (parts.length == 2) {
            String[] params = parts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals("token")) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

}
