package com.mvc.service;

import com.mvc.annotation.ioc.MyService;
import com.mvc.annotation.mypointcut.MyPointCut;

@MyService
public interface LoginService { 
	
	@MyPointCut
	public void validate(String id,String pwd);
 
}
