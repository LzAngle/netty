package edu.ylh.netty.t2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static edu.ylh.netty.t2.TestByteBuf.log;

/**
 * @author 靓仔
 * @date 2023/5/17
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {

        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{'a','b','c','d','e'});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{'f','g','h','i','j'});

        /*ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(buf1).writeBytes(buf2);
        log(buffer);*/
        // 上面的代码等价于下面的代码
        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        buffer.addComponents(true,buf1, buf2);
        log(buffer);

    }

}
