package com.mvc.service;

import com.mvc.annotation.MyCompant;
import com.mvc.annotation.Myautowrited;
import com.mvc.entry.EntryTest;
@MyCompant
public class TestServiceImpl implements TestService {
	
	@Myautowrited
	private EntryTest entryTest;
	@Override
	public void validate(String id,String pwd){
		
		System.out.println(entryTest);
		System.out.println("Test=====id:"+id+"\t"+"pwd:"+pwd);
	}
}
