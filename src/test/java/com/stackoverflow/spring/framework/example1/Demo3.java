package com.stackoverflow.spring.framework.example1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.stackoverflow.spring.framework.example1.yyy.A2;

public class Demo3 {

	public static void main(String[] args) {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext("demo3.xml");
		A2 a2 = context.getBean(A2.class);
	}

}
