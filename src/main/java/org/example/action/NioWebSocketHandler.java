package org.example.action;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class NioWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            // Ensure the request is a GET request and handle only if it's an upgrade request
            if (httpRequest.method() == HttpMethod.GET &&
                    httpRequest.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true)) {
                // Extract the URI from the request
                String uri = httpRequest.uri();
                httpRequest.setUri("/chat");
                String token = getTokenFromUri(uri);
                System.out.println("Received WebSocket Upgrade request, Token: " + token);
            }
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
