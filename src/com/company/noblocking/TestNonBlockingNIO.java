package com.company.noblocking;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @program: Java_NIO
 * @description: 练习非阻塞式NIO
 * @author: Mr.Qiu
 * @create: 2020-08-24 23:20
 **/

public class TestNonBlockingNIO {

    @Test
    public void client(){
        SocketChannel socketChannel = null;
        Scanner scanner = null;
        try {
//        创建通道
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8859));
//        设置为非阻塞式
            socketChannel.configureBlocking(false);

//        创建缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
//        写出数据
/*
            byteBuffer.put("你好，这是非阻塞式I/O练习！".getBytes());
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
*/
            scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                byteBuffer.put((LocalDateTime.now().toString()+":"+scanner.next()).getBytes());
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                try {
                    scanner.close();
                } catch (Exception e) {
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


    @Test
    public void server(){
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        try {
//        创建通道
            serverSocketChannel = ServerSocketChannel.open();
//        切换非阻塞模式
            serverSocketChannel.configureBlocking(false);
//        绑定连接
            serverSocketChannel.bind(new InetSocketAddress(8859));
//        创建选择器
            selector = Selector.open();

//        将通道注册到选择器上，并且指定“监听事件”
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

//        轮询式的获取选择器上已经“准备就绪”的事件
            while (selector.select()>0){
                //            获取当前选择器中所有注册的“选择键（已就绪的监听事件）”
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable()){
                        //                    获取连接
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        //                    将客户端连接注册到选择器上
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }else if (selectionKey.isReadable()){
                        //                    获取当前选择器上“读就绪”状态的通道
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    //                    读数据
                        while (socketChannel.read(byteBuffer)>0){
                            byteBuffer.flip();
                            System.out.println(new String(byteBuffer.array(),0,byteBuffer.limit()));
                            byteBuffer.clear();
                        }
                    }
    //                删除已经触发的选择键SelectionKey
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (selector != null) {
                try {
                    selector.close();
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
