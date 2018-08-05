package com.mvc.service;

public class LoginServiceImpl implements LoginService {

	@Override
	public void validate(String id,String pwd){
		System.out.println("id:"+id+"\t"+"pwd:"+pwd);
	}
}
