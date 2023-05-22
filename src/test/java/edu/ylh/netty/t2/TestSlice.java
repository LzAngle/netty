package edu.ylh.netty.t2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author 靓仔
 * @date 2023/5/17
 */
public class TestSlice {
    public static void main(String[] args) {

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        TestByteBuf.log(buf);

        // 在切片过程中，没有发生数据复制
        ByteBuf f1 = buf.slice(0, 5);
        ByteBuf f2 = buf.slice(5, 5);
        TestByteBuf.log(f1);
        TestByteBuf.log(f2);
        // f1.writeBytes("z".getBytes());// 会报错，因为切片后的buf只能操作自己的数据

        System.out.println("=================");
        f1.setByte(0, 'b');
        TestByteBuf.log(f1);
        TestByteBuf.log(buf);

    }
}
