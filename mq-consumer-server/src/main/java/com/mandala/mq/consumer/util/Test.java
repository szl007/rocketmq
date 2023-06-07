package com.mandala.mq.consumer.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @Author songzhenliang
 * @Date 2023-05-22 09:36
 */
public class Test {

    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader("/Users/songzhenliang/Downloads/hdxs55.txt"));
//        String line = reader.readLine();
//        while (line != null){
//            line =  new String(line.getBytes("iso8859-1"), "gbk");
//            System.out.println(line);
//            line = reader.readLine();
//        }
//        reader.close();

        String path = "/Users/songzhenliang/Downloads/22.txt";
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader iReader = new InputStreamReader(fis, "gbk");
        char[] chars = new char[1024];
        String content = "";
        while (iReader.read(chars) > 0){
            content += new String(chars);
        }

        System.out.println(content);

    }
}
