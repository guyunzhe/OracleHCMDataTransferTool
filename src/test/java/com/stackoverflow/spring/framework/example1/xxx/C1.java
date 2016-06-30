package com.stackoverflow.spring.framework.example1.xxx;
import org.springframework.stereotype.Component;

@Component
public class C1 {
	public C1() {
		System.out.println("creating bean C1: " + this);
	}
}
