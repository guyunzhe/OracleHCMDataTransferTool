package com.stackoverflow.spring.framework.example1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.stackoverflow.spring.framework.example1.yyy.A1;

public class Demo2 {

	public static void main(String[] args) {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext("demo2.xml");
		A1 a1 = context.getBean("aBean", A1.class);
	}

}
