package com.stackoverflow.spring.framework.example1.xxx;
import org.springframework.stereotype.Component;

@Component
public class B1 {
	public B1() {
		System.out.println("creating bean B1: " + this);
	}
}
