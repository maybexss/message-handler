package com.sweeky.message.handler.framework.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileTest {
    public static void main(String[] args) {
        String filePath = "D:\\test\\test.bin";
        RandomAccessFile randomAccessFile = null;
        RandomAccessFile readFile = null;
        try {
            randomAccessFile = new RandomAccessFile(new File(filePath), "rw");
            randomAccessFile.setLength(1024);
            randomAccessFile.seek(0L);
            for (int i = 1; i <= 10; i++) {
                randomAccessFile.writeLong(i * 100 + 1);
                randomAccessFile.writeLong(i * 100 + 2);
                randomAccessFile.writeLong(i * 100 + 3);
                randomAccessFile.writeInt(i * 100 + 4);
                randomAccessFile.writeInt(i * 100 + 5);
            }
            randomAccessFile.getFD().sync();
            randomAccessFile.close();
            readFile = new RandomAccessFile(new File(filePath), "rw");
            readFile.seek(0L);
            long offset = 0L;
            while (true) {
                if (offset >= readFile.length()) {
                    break;
                }
                long value = readFile.readLong();
                if (value == 0) {
                    readFile.seek(offset);
                    System.out.println("offset: " + offset + ", value: " + value);
                    break;
                }
                System.out.println(value);
                System.out.println(readFile.readLong());
                System.out.println(readFile.readLong());
                System.out.println(readFile.readInt());
                System.out.println(readFile.readInt());
                offset += 32;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (readFile != null) {
                    readFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
