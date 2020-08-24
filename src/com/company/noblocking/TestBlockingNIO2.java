package com.company.noblocking;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @program: Java_NIO
 * @description: 熟练使用阻塞式NIO 传输且响应
 * @author: Mr.Qiu
 * @create: 2020-08-24 22:30
 **/

public class TestBlockingNIO2 {

//    我这里使用transferTo流于流进行对接，简化写法
    @Test
    public void client(){
        SocketChannel socketChannel = null;
        FileChannel fileChannel = null;
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8859));
            fileChannel = FileChannel.open(Paths.get("Java+NIO.pdf"), StandardOpenOption.READ);

            fileChannel.transferTo(0, fileChannel.size(), socketChannel);

            //因为式阻塞式，所以发完要告诉服务端，已经发完文件/数据了
            socketChannel.shutdownOutput();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//            把返回的数据读出来
            int len=0;
            while ((len=socketChannel.read(byteBuffer))!=-1) {
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(),0,len));
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


//    很遗憾，SocketChannel无法携带size数据，所以无法使用FileChannel.transferFrom对接,老办法循环写
    @Test
    public void server(){
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        FileChannel fileChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8859));
            socketChannel = serverSocketChannel.accept();
            fileChannel = FileChannel.open(Paths.get("Java_NIO.pdf"),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (-1!=socketChannel.read(byteBuffer)){
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
                byteBuffer.clear();
            }

//        传输完成后，发挥数据响应
            byteBuffer.put("文件传输成功".getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocketChannel != null) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
