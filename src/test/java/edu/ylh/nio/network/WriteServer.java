package edu.ylh.nio.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author 靓仔
 * @date 2023/5/15
 */
public class WriteServer {
    public static void main(String[] args) throws IOException {

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {

            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector,0,null);
                    scKey.interestOps(SelectionKey.OP_READ);

                    // 1.像客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 3000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    while (buffer.hasRemaining()) {
                        // 2.返回值表示实际写入的字节数
                        int write = sc.write(buffer);
                        System.out.println(write);

                        // 3.判断是否还有剩余的数据
                        if (buffer.hasRemaining()) {
                            // 4.关注可写事件        1                       4
                            scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                            // 5.将未写完的数据挂到 scKey 上
                            scKey.attach(buffer);
                        }

                    }
                }else if (key.isWritable()){

                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println(write);
                    // 6. 清理操作
                    if (!buffer.hasRemaining()){
                        // 需要清理buffer(附件)
                        key.attach(null);
                        // 不需要关注可写事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }
                }
            }

        }

    }

}
