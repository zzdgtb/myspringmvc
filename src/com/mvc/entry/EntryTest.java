package com.mvc.entry;

import com.mvc.annotation.ioc.MyCompant;

@MyCompant
public class EntryTest {

	private String name;
	
	private String id;

	public String getName() {
		return name;
	}
	

	public void setName(String name) {
		this.name = name;
	}
	

	public String getId() {
		return id;
	}
	

	public void setId(String id) {
		this.id = id;
	}
	
	
	
}
