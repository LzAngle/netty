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
import java.util.Scanner;

/**
 * @author 靓仔
 * @date 2023/5/16
 */
@Slf4j
public class CloseFutureClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
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
        Channel channel = channelFuture.sync().channel();
        log.debug("{}",channel);
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true){
                String next = scanner.nextLine();
                if (next.equals("q")){
                    channel.close(); // close 是异步操作 可能是1秒之后
                    log.debug("处理关闭之后的操作");// 不能只这里善后
                    break;
                }
                channel.writeAndFlush(next);
            }
        },"input").start();
        // log.debug("处理关闭之后的操作");// 不能只这里善后

        // 获取 CloseFuture 对象，1.异步处理结果 2.同步等待结果
        ChannelFuture closeFuture = channel.closeFuture();
        /*System.out.println("waiting close...");
        closeFuture.sync();
        log.debug("处理关闭之后的操作");// 在这之后可以安全的执行善后操作*/

        closeFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            log.debug("处理关闭之后的操作");// 在这之后可以安全的执行善后操作
            group.shutdownGracefully();
        });

    }

}
