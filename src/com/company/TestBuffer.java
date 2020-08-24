package com.company;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 *  一、缓冲区(Buffer):再 Java NIO 中负责数据的存取。缓冲区就是数组。用于存储不同数据类型的数据
 *
 *  根据数据类型不同(boolean 除外) ，提供了相应类型的缓冲区：
 *  ByteBuffer
 *  CharBuffer
 *  ShortBuffer
 *  IntBuffer
 *  LongBuffer
 *  FloatBuffer
 *  DoubleBuffer
 *
 *
 *  上述缓冲区的管理方式几乎一致，通过allocate() 获取缓冲区
 *
 *
 *  二、缓冲区存取数据的两个核心方法：
 *  put()：存入数据到缓冲区中
 *  get()：获取缓冲区中的数据
 *
 *  四、缓冲区中的四个核心属性：
 *  capacity：容量，表示缓冲区中最大的存储数据的容量。一旦声明不能改变。
 *  limit：界限，表示缓冲区中可以操作数据的大小。（limit 后数据不能进行读写）
 *  position：位置，表示缓冲区中正在操作数据的位置
 *
 *  mark；标记，表示记录当前 position 的位置。可以通过reset() 恢复到mark 的位置
 *
 *  0 <= mark <= position <= limit <= capacity
 *
 *  五、直接缓冲区于非直接缓冲区：
 *  非直接缓冲区：通过allocate() 方法分配缓冲区，将缓冲区建立再JVM的内存中
 *  直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中
 *
 *
 *
 *
 *
 *
 *
 *
 */





public class TestBuffer {

    @Test
    public void test1(){
        String str = "hello";

        System.out.println("-------------------------allocate()-----------------------------");
//        1.allocate()创建缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        System.out.println("-------------------------put() 写,默认就是写模式-----------------------------");
//        2.put()写数据
        buf.put(str.getBytes());
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        System.out.println("-------------------------flip() 切换成读模式-----------------------------");
//        3.flip()切换成读数据模式
        buf.flip();
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        System.out.println("-------------------------get() 进行读数据-----------------------------");
        byte[] bb = new byte[buf.limit()];
//        4.get()读取缓冲区数据
        buf.get(bb);
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());
        System.out.println(new String(bb));

//        5.rewind() 可重复读数据，将position归零
        System.out.println("-------------------------rewind() 重置position，可以用来重读数据-----------------------------");
        buf.rewind();
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

//        6.clear()清空缓冲区 里面的数据并没有消失，但是被遗忘，可以被覆盖重写
        System.out.println("-------------------------clear() 清空缓冲区 重置了position，和limit-----------------------------");
        buf.clear();
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

    }

    @Test
//    使用mark和reset
    public void test2(){
        String str = "abcd";

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes());

        buffer.flip();
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst,0,2);
        System.out.println(new String(dst));
        System.out.println(buffer.position());

        buffer.mark();
        buffer.get(dst,2,2);
        System.out.println(new String(dst,0,2));
        System.out.println(buffer.position());
        buffer.reset();
        System.out.println(buffer.position());

//        判断缓冲区中是否还有剩余数据
        if(buffer.hasRemaining()){
//            获取缓冲区中可以操作的数量
            System.out.println(buffer.remaining());
        }

    }

    @Test
//    创建直接缓冲区
    public void test3(){
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

//        判断缓冲区是否是直接缓冲区
        System.out.println(buffer.isDirect());
    }



}
