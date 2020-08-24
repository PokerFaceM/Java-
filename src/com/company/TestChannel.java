package com.company;

/**
 * @program: Java_NIO
 * @description: 通道练习
 * @author: Mr.Qiu
 * @create: 2020-08-24 16:04
 **/


import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * 一、通道(Channel)：用于源节点于目标节点的连接。在Java NIO 中负责缓冲区数据的传输。Channel本身不存储数据，因此需要配合缓冲区进行传输
 *
 *      结合书本《计算机系统结构》我的理解：通道就是一个由I/O设备执行的程序。
 *      是DMA的扩展。DMA一般只能针对一各I/O设备，而通道可以有很多方式，控制多台I/O设备。
 *      通道是一个程序，由I/O设备执行的程序
 *
 *      通道分为：
 *          选择通道            ：一般用于优先级高的I/O设备，独占通道
 *          字节多路通道         ：一般用于低速的设备，可以连接多台低速设备，进行轮流执行
 *          数组多路通道（成组多路）：适合于连接多态磁盘等高速设备
 *
 * 二、通道的主要实现类
 *  java.nio.channels.Channel 接口：
 *      |--FileChannel
 *      |--SocketChannel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 *
 * 三、获取通道
 * 1.Java 针对支持通道的类提供了getChannel() 方法
 *      本地I/O：
 *      FileInputStream/FileOutputStream
 *      RandomAccessFile
 *
 *      网络I/O：
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 *
 * 2.在JDK 1.7中的NIO.2 针对各个通道提供了静态方法open()
 * 3.在JDK 1.7中的NIO.2 的Files工具类的newByteChannel()
 *
 *
 * 四、通道之间的数据传输
 * transferFrom()
 * transferTo()
 *
 *
 * 五、分散(Scatter)于聚集(Gather)
 * 分散读取（Scattering Reads）:将通道中的数据分散到多个缓冲区中
 * 聚集写入（Gathering Writes）:将多个缓冲区中的数据聚集到通道中
 *
 *
 * 六、字符集：Charset
 * 编码：字符串->字节数组
 * 解码：字节数组->字符串
 *
 */
public class TestChannel {

    @Test
    public void test6(){
        Charset cs1 = Charset.forName("UTF-8");
//        通过Charset获取编码器和解码器
        CharsetEncoder charsetEncoder = cs1.newEncoder();
        CharsetDecoder charsetDecoder = cs1.newDecoder();

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("安康鱼宇宙第一、");
        charBuffer.flip();

//        编码
        ByteBuffer byteBuffer=null;
        try {
             byteBuffer = charsetEncoder.encode(charBuffer);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

        System.out.println(byteBuffer.toString());
//        解码
        CharBuffer deCodeCharBuffer =null;
        try {
            deCodeCharBuffer= charsetDecoder.decode(byteBuffer);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }

        System.out.println(byteBuffer.toString());
        deCodeCharBuffer.rewind();
        System.out.println(deCodeCharBuffer.toString());

    }

    @Test
//    字符集
    public void test5(){
        SortedMap<String, Charset> map = Charset.availableCharsets();

        Set<Map.Entry<String, Charset>> entries = map.entrySet();

        for(Map.Entry<String,Charset> entry: entries){
            System.out.println(entry.getKey()+"="+entry.getValue());
        }

    }




    @Test
//    分散读取和聚集写入
    public void test4() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("Junit依赖.txt", "rw");
//        获取通道
        FileChannel inChannel = raf.getChannel();
//        分配缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(100);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);
//        进行分散读取
        ByteBuffer[] buffers = {buffer1,buffer2};
        inChannel.read(buffers);

        for(ByteBuffer byteBuffer:buffers){
            byteBuffer.flip();
        }

        System.out.println(new String(buffer1.array()));
        System.out.println(new String(buffer2.array(),0,buffer2.limit()));
        System.out.println(buffer1.position()+"-"+buffer1.limit());
        System.out.println(buffer2.position()+"-"+buffer2.limit()+"--------------");

//        进行聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("Junit_dependency.txt", "rw");
        FileChannel outChannel = raf2.getChannel();
        outChannel.write(buffers);

        System.out.println(buffer1.position()+"-"+buffer1.limit());
        System.out.println(buffer2.position()+"-"+buffer2.limit());

        inChannel.close();
        raf.close();

    }


    @Test
//    通道之间数据传输
    public void test3(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("晓组织.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("Akatsuki.jpg"),
                    StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);

            inChannel.transferTo(0, inChannel.size(),outChannel);
//            outChannel.transferFrom(inChannel, 0, inChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }



    @Test
//    使用直接缓冲区方式，完成文件的复制（内存映射文件的方式）
    public void test2(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("索隆.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("RoronoaZoro.jpg"),
                    StandardOpenOption.READ,StandardOpenOption.WRITE,StandardOpenOption.CREATE);

//        使用.map创建内存映射的缓冲区
            MappedByteBuffer inMappedByteBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            MappedByteBuffer outMappedByteBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());


//        直接对缓冲区进行数据的读写操作
            byte[] dst = new byte[inMappedByteBuffer.limit()];
            inMappedByteBuffer.get(dst);

            outMappedByteBuffer.put(dst);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Test
//    利用通道完成文件的复制，非直接缓冲区
    public void test1(){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream("自考.png");
            fos = new FileOutputStream("zikao.png");

//        1.获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

//        2.创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);

//        3.将通道中的数据存入缓冲区中
            while(inChannel.read(buffer)!=-1){
    //        将缓冲区的数据写到通道中
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();//清空缓冲区

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
