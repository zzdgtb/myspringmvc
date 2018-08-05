package com.mvc.service;

import com.mvc.annotation.MyService;

@MyService
public interface LoginService {   
	
public void validate(String id,String pwd);
 
}
