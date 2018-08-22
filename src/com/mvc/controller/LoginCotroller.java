package com.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.annotation.ioc.Myautowrited;
import com.mvc.annotation.mvc.Mycontroller;
import com.mvc.annotation.mvc.Myparamer;
import com.mvc.annotation.mvc.RequestMapping;
import com.mvc.service.LoginService;

@Mycontroller
@RequestMapping("/login")
public class LoginCotroller {
	
	
	@Myautowrited
	private LoginService loginService;
	
	@RequestMapping("/valadate")
	public void validatelogin(@Myparamer(value = "id")String loginid,@Myparamer(value = "pwd")String password,HttpServletRequest request, HttpServletResponse response){
		
		loginService.validate(loginid, password);
	}
}
