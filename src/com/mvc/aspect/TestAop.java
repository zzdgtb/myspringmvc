package com.mvc.aspect;

import com.mvc.annotation.aop.After;
import com.mvc.annotation.aop.Before;
import com.mvc.annotation.aop.MyAspect;

@MyAspect
public class TestAop {

	@Before(annotation = "com.mvc.annotation.mypointcut.MyPointCut")
	public void beforesay(){
		System.out.println("beforesay");
	}
	@After(annotation = "com.mvc.annotation.mypointcut.MyPointCut")
	public void aftersay(){
		System.out.println("aftersay");
	}
}
