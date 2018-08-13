package com.mvc.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.annotation.Myautowrited;
import com.mvc.annotation.Mycontroller;
import com.mvc.annotation.Myparamer;
import com.mvc.annotation.RequestMapping;
import com.mvc.service.LoginService;
import com.mvc.service.TestService;

@Mycontroller
@RequestMapping("/test/index")
public class TestCotroller {
	
	
	@Myautowrited
	private TestService testService;
	
	@RequestMapping("/query")
	public void validatelogin(String loginid,@Myparamer(value = "pwd")String password,HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		testService.validate(loginid, password);
		
		response.getWriter().write("success");
	}
}
