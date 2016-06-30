package com.stackoverflow.spring.framework.example1.yyy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stackoverflow.spring.framework.example1.xxx.B1;
import com.stackoverflow.spring.framework.example1.xxx.C1;

@Component
public class A2 {
	private B1 bbb;
	private C1 ccc;

	public A2() {
		System.out.println("creating bean A2: " + this);
	}

	@Autowired
	public void setBbb(B1 bbb) {
		System.out.println("setting A.bbb with " + bbb);
		this.bbb = bbb;
	}

	@Autowired
	public void setCcc(C1 ccc) {
		System.out.println("setting A.ccc with " + ccc);
		this.ccc = ccc;
	}
}
