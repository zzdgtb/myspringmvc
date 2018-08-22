package com.mvc.service;

import com.mvc.annotation.ioc.MyService;

@MyService
public interface TestService {   
	
public void validate(String id,String pwd);
 
}
