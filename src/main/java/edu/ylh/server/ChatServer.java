package cn.ylh.server;

import cn.ylh.protocol.MessageCodecSharable;
import cn.ylh.protocol.ProtocolFrameDecoder;
import cn.ylh.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        // nio事件工作组boss，用于接收请求
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // nio事件工作组worker，用于去处理请求
        NioEventLoopGroup worker = new NioEventLoopGroup();
        // 日志打印处理器
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        // 消息编解码处理器
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        // 登录请求消息处理器
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        // 私聊消息发送处理器
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        // 创建群聊处理器
        GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        // 加入群聊处理器
        GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        // 查询群成员处理器
        GroupMembersRequestMessageHandler GROUP_MEMBER_HANDLER = new GroupMembersRequestMessageHandler();
        // 群发消息处理器
        GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        // 退出群聊处理器
        GroupQuitRequestMessageHandler GROUP_QUITE_HANDLER = new GroupQuitRequestMessageHandler();
        // 退出程序处理器
        QuitHandler QUIT_HANDLER = new QuitHandler();
        try {
            // 服务端启动器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 选择channel类型
            serverBootstrap.channel(NioServerSocketChannel.class);
            // 添加工作组  boss，用于接收请求； worker，用于处理请求
            serverBootstrap.group(boss, worker);
            // 初始化自定义业务的处理器
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 添加连接状态判断处理器
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
                    ch.pipeline().addLast(new IdleStateHandler(5,0,0));
                    // 添加入栈出栈处理器 用来处理用户点击x退出和正常输入退出情况
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        // 用来出发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了读空闲事件
                            if (event.state() == IdleState.READER_IDLE) {
                                log.debug("已经 5s 没有读到数据了");
                                // 关闭与客户端的连接
                                ctx.channel().close();
                            }
                        }
                    });
                    // 添加自定义协议处理器
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    // 添加日志打印处理器
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    // 添加消息编解码处理器
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    // 添加登录处理器
                    ch.pipeline().addLast(LOGIN_HANDLER);
                    // 添加私聊处理器
                    ch.pipeline().addLast(CHAT_HANDLER);
                    // 添加创建群聊处理器
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    // 添加加入群聊处理器
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    // 添加查询群成员处理器
                    ch.pipeline().addLast(GROUP_MEMBER_HANDLER);
                    // 添加群发消息处理器
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                    // 添加退出群聊处理器
                    ch.pipeline().addLast(GROUP_QUITE_HANDLER);
                    // 添加退出程序处理器
                    ch.pipeline().addLast(QUIT_HANDLER);
                }
            });
            // 服务器绑定 8080 端口，同步等待(阻塞)，获得channel
            Channel channel = serverBootstrap.bind(8080)
                    .sync()
                    .channel();
            // 关闭连接
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            // 关闭工作组，释放资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
