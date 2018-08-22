package com.mvc.proxy;


/**
 * jdk动态代理工厂
 * @author 
 *
 */
public class SimpleProxyFactory {
	
	/**
	 * 获取代理对象
	 * @param targetClass
	 * @param aspectClass
	 * @return
	 */
	public static Object newInstance(Object targetClass,Object aspectClass){
		
		return new CustomHandle(targetClass,aspectClass).getProxy();
	}

}
