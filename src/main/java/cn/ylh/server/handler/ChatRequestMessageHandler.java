package cn.ylh.server.handler;

import cn.ylh.message.ChatRequestMessage;
import cn.ylh.message.ChatResponseMessage;
import cn.ylh.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: LzAngle
 * Date: 23:17 2023/5/20
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else {
            ctx.writeAndFlush(new ChatResponseMessage(false,"对方用户不存在或不在线"));
        }
    }
}
