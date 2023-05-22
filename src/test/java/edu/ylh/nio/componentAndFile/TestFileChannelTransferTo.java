package edu.ylh.nio.componentAndFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author 靓仔
 * @date 2023/5/11
 */
public class TestFileChannelTransferTo {

    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel()
        ) {
            // 文件大小
            long size = from.size();

            // 方式一：
            // 效率高，底层会利用操作系统的零拷贝进行优化
            // 参数：inputChannel的起始位置，传输数据的大小，目的channel
            // 返回值为传输的数据的字节数
            // transferTo一次只能传输2G的数据
            from.transferTo(0,size,to);

            // 方式二：
            // 分多次传输
            for(long left = size;left > 0;){
                System.out.println("position:" + (size - left) + " left:" + left);
                left -= from.transferTo(size - left,size,to);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
