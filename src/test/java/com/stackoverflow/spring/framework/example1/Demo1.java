package com.stackoverflow.spring.framework.example1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.stackoverflow.spring.framework.example1.yyy.A;

public class Demo1 {
	//old style spring
	public static void main(String args[]) {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext("demo1.xml");
		A a1 = context.getBean("aBean", A.class);
	}
}