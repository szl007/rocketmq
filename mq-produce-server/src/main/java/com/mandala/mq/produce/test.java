package com.mandala.mq.produce;

import java.io.*;

/**
 * 读取文件内容与本项目无关
 * @Author songzhenliang
 * @Date 2023-06-06 16:03
 */
public class test {
    public static void main(String[] args) throws IOException {
        String filePath = "/Users/songzhenliang/Downloads/11.txt";
        InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath),"GBK");//file为文件对象
        BufferedReader reader = new BufferedReader(isr);
        String tempString = null;
        int line = 1;
        while ((tempString = reader.readLine()) != null) { // 一次读入一行，直到读入null为文件结束
            if(line < 20000){
                System.out.println(tempString);
            }
            //localBody=localBody+tempString+"\n";
            line ++;
        }

    }
}
