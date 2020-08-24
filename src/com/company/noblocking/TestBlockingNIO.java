package com.company.noblocking;

/**
 * @program: Java_NIO
 * @description:先练习阻塞式  NIO
 * @author: Mr.Qiu
 * @create: 2020-08-24 21:16
 **/

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
 * 一、使用NIO 完成网络通信的三个核心
 *
 * 1.通道（Channel）：负责连接
 *      java.nio.channels.Channel接口
 *          |--SelectableChannel
 *              |--SocketChannel        //对应于TCP协议的
 *              |--ServerSocketChannel  //对应于TCP协议的
 *              |--DatagramChannel      //对应于UDP协议的
 *
 *              |--Pipe.SinkChannel
 *              |--Pipe.SourceChannel
 *
 * 2.缓冲区（Buffer）：负责数据的存取
 *
 * 3.选择器（Selector）：式 SelectableChannel 的多路复用器。用于监控 SelectableChannel 的IO状况
 *
 *
 *
 */

public class TestBlockingNIO {

    @Test
    public void client(){

        SocketChannel socketChannel = null;
        FileChannel fileChannel = null;
        try {
//            创建TCP客户端Channel
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8859));
            fileChannel = FileChannel.open(Paths.get("Java+NIO.pdf"), StandardOpenOption.READ);

//            创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while(fileChannel.read(buffer)!=-1){
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
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

    @Test//先启动服务器
    public void server(){
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        FileChannel fileChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8859));
            socketChannel = serverSocketChannel.accept();
            fileChannel = FileChannel.open(Paths.get("Java-NIO.pdf"),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while(socketChannel.read(byteBuffer)!=-1){
                byteBuffer.flip();
                fileChannel.write(byteBuffer);
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
