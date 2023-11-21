package org.example.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NioWebSocketChannelPool {

    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    Map<Channel, String> channelsMap = new HashMap<>();

    public void setTokenForChannel(String token, Channel channel) {
        if (token.isEmpty()) { return; }
        Channel newChannel = channels.find(channel.id());
        if (newChannel != null) {
            channelsMap.put(newChannel, token);
        }
    }

    public String getTokenForChannel(Channel channel) {
        Channel newChannel = channels.find(channel.id());
        if (newChannel != null) {
            return channelsMap.get(newChannel);
        }
        return null;
    }

    /**
     * 新增一个客户端通道
     *
     * @param channel
     */
    public void addChannel(Channel channel) {
        channels.add(channel);
        channelsMap.put(channel, null);
    }

    /**
     * 移除一个客户端通道
     *
     * @param channel
     */
    public void removeChannel(Channel channel) {
        channels.remove(channel);
        channelsMap.remove(channel);
    }

    public void postNoticeMessage(String message) {
        if (message.isEmpty()) { return; }
        for (Channel channel: channels) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
        }
    }

}
