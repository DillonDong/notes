package com.xuecheng.search;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class IOUtil {
    private MappedByteBuffer[] mappedBufArray;
    private int count = 0;
    private int number;
    private FileInputStream fileIn;
    private long fileLength;
    private int arraySize;
    private byte[] array;

    public IOUtil(String fileName, int arraySize) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        FileChannel fileChannel = fileIn.getChannel();
        this.fileLength = fileChannel.size();
        long regionSize = 1024*1024*500;// 映射区域的大小  不能超过2G
        this.number = (int) Math.ceil((double) fileLength / (double) regionSize);   //内存映射文件总块数
        this.mappedBufArray = new MappedByteBuffer[number];// 内存文件映射数组
        long preLength = 0;
        for (int i = 0; i < number; i++) {// 将文件的连续区域映射到内存文件映射数组中
            if (fileLength - preLength < (long) regionSize) {
                regionSize = fileLength - preLength;// 最后一片区域的大小
            }
            mappedBufArray[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
            preLength += regionSize;// 下一片区域的开始
        }
        this.arraySize = arraySize;
    }

    public int read() throws IOException {
        if (count >= number) {
            return -1;
        }
        int limit = mappedBufArray[count].limit();      //内存映射文件大小  1024
        int position = mappedBufArray[count].position();
        if (limit - position > arraySize) {
            array = new byte[arraySize];
            System.out.println(array);
            mappedBufArray[count].get(array);
            return arraySize;
        } else {// 本内存文件映射最后一次读取数据
            array = new byte[limit - position];
            mappedBufArray[count].get(array);
            if (count < number) {
                count++;// 转换到下一个内存文件映射
            }
            return limit - position;
        }
    }
    public void close() throws IOException {
        fileIn.close();
        array = null;
    }
    public byte[] getArray() {
        return array;
    }
    public long getFileLength() {
        return fileLength;
    }
    public static void main(String[] args) throws IOException {

        String filepath = "E:\\上课\\品优购\\资源\\配套软件.rar";

        IOUtil reader = new IOUtil(filepath, 1000);
        long start = System.nanoTime();
        int i = 0 ;
        while (reader.read() != -1){
            i = i +1;
            String str = new String(reader.getArray());
            String[] lines = str.split("\r");
            for(String line : lines){
                System.out.println(line);
            }
        };
        reader.close();
    }
}
