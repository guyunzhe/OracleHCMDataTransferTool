package com.stackoverflow.spring.framework.example1.yyy;

import org.springframework.beans.factory.annotation.Autowired;

import com.stackoverflow.spring.framework.example1.xxx.B;
import com.stackoverflow.spring.framework.example1.xxx.C;

public class A1 {
	private B bbb;
	private C ccc;

	public A1() {
		System.out.println("creating bean A1: " + this);
	}

	@Autowired
	public void setBbb(B bbb) {
		System.out.println("setting A1.bbb with " + bbb);
		this.bbb = bbb;
	}

	@Autowired
	public void setCcc(C ccc) {
		System.out.println("setting A1.ccc with " + ccc);
		this.ccc = ccc;
	}
}
