package edu.ylh.nio.network;

import edu.ylh.nio.componentAndFile.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author 靓仔
 * @date 2023/5/12
 */
public class ServerSelector {
    public static void main(String[] args) throws IOException {
        // 1、创建selector，管理多个 channel
        Selector selector = Selector.open();

        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2、建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件
        SelectionKey scKey = ssc.register(selector, 0, null);
        // key 只关注 accept 事件
        scKey.interestOps(SelectionKey.OP_ACCEPT);
        System.out.println("register key:" + scKey);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 3、select 方法,没有事件发生，线程阻塞，有事件，线程才会恢复运行
            // select 在事件未处理时，它不会阻塞
            selector.select();
            // 4、处理事件，selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();// accept, read
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理key 时，要从 selectedKeys 集合中删除，否则下次处理就会有问题
                iter.remove();
                System.out.println("key:" + key);
                // 5、区分事件类型
                if (key.isAcceptable()) {// 如果是 accept
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    sc.register(selector,0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    System.out.println("SocketChannel:" + sc);
                } else if (key.isReadable()) {// 如果是 read
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();// 拿到触发事件的channel
                        System.out.println("SocketChannel:" + channel);
                        buffer.clear();
                        while (true) {
                            int read = channel.read(buffer);// 如果是正常断开，read 的方法返回值是-1
                            if (read > 0) {
                                buffer.flip();
                                ByteBufferUtil.debugAll(buffer);
                            } else if (read == 0) {
                                break;
                            } else {
                                channel.close();
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 因为客户端断开连接，所以需要将 key 取消 (从 selector 的 keys 集合中真正删除 key)
                    }
                }
                // key.cancel();
            }
        }

    }
}
