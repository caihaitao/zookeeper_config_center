package com.cc.architecture;

import com.alibaba.fastjson.JSON;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class ArchitectureApplicationTests {
	protected void printResult(Object o) {
		if(Objects.isNull(o)) {
			System.err.println("======>"+o);
		}
		System.err.println("get result:"+JSON.toJSONString(o));
	}
}
