package com.mandala.mq.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class MqConsumerServerApplicationTests {

	@Test
	void contextLoads() throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader("/Users/songzhenliang/Downloads/hdxs55.txt"));
		String line = reader.readLine();
		while (line != null){
			System.out.println(line);
			line = reader.readLine();
		}
		reader.close();
	}

}
