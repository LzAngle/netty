package edu.ylh.netty.t2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author 靓仔
 * @date 2023/5/18
 */
public class TestLengthFieldDecoder {

    public static void main(String[] args) {

        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        // 4个字节的长度，长度为10，内容为“hello,world”
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer,"hello,world");
        send(buffer,"Hi!");
        channel.writeInbound(buffer);
    }

    private static void send(ByteBuf buffer,String content) {
        byte[] bytes = content.getBytes(); // 实际内容
        int length = bytes.length;// 实际内容长度
        buffer.writeInt(length);
        buffer.writeBytes(bytes);
    }

}
