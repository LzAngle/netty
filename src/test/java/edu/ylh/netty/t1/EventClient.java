package edu.ylh.netty.t1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author 靓仔
 * @date 2023/5/16
 */
@Slf4j
public class EventClient {

    public static void main(String[] args) throws InterruptedException {
        // 2.带有Future,Promise的类型都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 在连接建立后被调用
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1. 连接到服务器
                // 异步非阻塞，main 发起调用，真正执行 connect 是 nio 线程
                .connect(new InetSocketAddress("localhost", 8080));// 1s 之后执行到这里

        // 2.1 使用 sync 方法同步处理结果
        /*channelFuture.sync();// 阻塞住当前线程，直到 nio 线程连接建立完毕
        // 无阻塞向下执行获取 channel
        Channel channel = channelFuture.channel();
        channel.writeAndFlush("hello,world");*/

        // 2.2 使用 addListener(回调对象) 处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            @Override // 在 nio 线程连接建立好之后调用 operationComplete
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                // 无阻塞向下执行获取 channel
                Channel channel = channelFuture.channel();
                log.debug("{}", channel);
                channel.writeAndFlush("hello,world");
            }
        });

    }

}
