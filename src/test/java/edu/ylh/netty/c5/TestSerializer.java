package edu.ylh.netty.c5;

import edu.ylh.config.Config;
import edu.ylh.message.LoginRequestMessage;
import edu.ylh.message.Message;
import edu.ylh.protocol.MessageCodec;
import edu.ylh.protocol.MessageCodecSharable;
import edu.ylh.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author 靓仔
 * @date 2023/5/22
 */
public class TestSerializer {

    public static void main(String[] args) {
        MessageCodecSharable CODE = new MessageCodecSharable();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODE, LOGGING);

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        // channel.writeOutbound(message);
        ByteBuf buf = messageToByteBuf(message);
        channel.writeInbound(buf);
    }

    public static ByteBuf messageToByteBuf(Message msg) {
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{1, 2, 3, 4});
        out.writeByte(1);
        out.writeByte(algorithm);
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        byte[] bytes = Serializer.Algorithm.values()[algorithm].serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        return out;
    }

}
