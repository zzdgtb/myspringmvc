package com.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.annotation.Myautowrited;
import com.mvc.annotation.Mycontroller;
import com.mvc.annotation.Myparamer;
import com.mvc.annotation.RequestMapping;
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
