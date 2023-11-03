package org.example.action.netty;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.example.config.UserLoginInterceptor;
import org.example.entity.Account;
import org.example.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
@ChannelHandler.Sharable
public class NioWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Autowired
    private NioWebSocketChannelPool webSocketChannelPool;

    @Autowired
    NettyServer nettyServer;

    @Resource
    AccountMapper accountMapper;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端连接：{}", ctx.channel().id());
        webSocketChannelPool.addChannel(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端断开连接：{}", ctx.channel().id());
        webSocketChannelPool.removeChannel(ctx.channel());
        super.channelInactive(ctx);
    }

    /**
     * 用于通知处理器当前通道上的数据读取操作已经完成的回调方法，
     * 可以在该方法中执行一些清理操作、发送响应或其他相关的逻辑。
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug("通道读取数据完毕 刷新通道：{}", ctx.channel().id());
        ctx.channel().flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 根据请求数据类型进行分发处理
        if (frame instanceof TextWebSocketFrame) {
            textWebSocketFrameHandler(ctx, (TextWebSocketFrame) frame);
        } else if (frame instanceof CloseWebSocketFrame) {
            closeWebSocketFrameHandler(ctx, (CloseWebSocketFrame) frame);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof  FullHttpRequest) {
            fullHttpRequestHandler(ctx, (FullHttpRequest) msg);
        }
        super.channelRead(ctx, msg);
    }

    /**
     * 处理连接请求，客户端WebSocket发送握手包时会执行这一次请求
     * @param ctx
     * @param request
     */
    private void fullHttpRequestHandler(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        Map<String, String> params = UserLoginInterceptor.RequestUriUtils.getParams(uri);
        log.debug("客户端请求参数：{}", params);

        // 判断请求路径是否跟配置中的一致
        if (nettyServer.getPath().equals(UserLoginInterceptor.RequestUriUtils.getBasePath(uri))) {
            // 因为有可能携带了参数，导致客户端一直无法返回握手包，因此在校验通过后，重置请求路径
            String token = params.get("token");
            Account account = tokenIsVaild(token);
            if (account != null) {
                webSocketChannelPool.setTokenForChannel(token, ctx.channel());
                log.debug("客户端 id:{} ，token：{}", ctx.channel().id(), token);
                request.setUri(nettyServer.getPath());
            } else {
                handCloseRemoveChannel(ctx);
            }
        } else {
            handCloseRemoveChannel(ctx);
        }
    }

    private void handCloseRemoveChannel(ChannelHandlerContext ctx) {
        ctx.close();
//        webSocketChannelPool.removeChannel(ctx.channel());
    }

    /**
     * 客户端发送断开请求处理
     * @param ctx
     * @param frame
     */
    private void closeWebSocketFrameHandler(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
        handCloseRemoveChannel(ctx);
    }

    private void textWebSocketFrameHandler(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        // 客户端发送过来的内容不进行业务处理，原样返回
        ctx.channel().writeAndFlush(frame.retain());
    }

    public Account tokenIsVaild(String token) {
        QueryWrapper<Account> query = new QueryWrapper<Account>();
        query.eq("token", token);
        query.select("id", "nickname"); // 指定要返回的字段
        query.last("limit 1");
        return accountMapper.selectOne(query);
    }

}
