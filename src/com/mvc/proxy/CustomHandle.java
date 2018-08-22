package com.mvc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.mvc.annotation.aop.After;
import com.mvc.annotation.aop.Before;



/**
 * jdk动态代理
 * @author 
 *
 */
public class CustomHandle implements InvocationHandler {
	
	/**
	 * 目标对象
	 */
	private Object target;
	
	/**
	 * 增强
	 */
	private Object aspectClass;
	
	/**
	 * 初始化
	 * @param target
	 * @param aspectClass
	 */
	public CustomHandle(Object target,Object aspectClass){
		this.target=target;
		this.aspectClass = aspectClass;
	}
	
	/**
	 * 返回代理对象
	 * @return
	 */
	public Object getProxy(){
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}
	
	/**
	 * 代理对象执行方法
	 */
	@Override
	public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
		
		//前置增强
		beforeInvoke(arg1,arg2);
		
		//业务方法
		Object rtn =arg1.invoke(target, arg2);
		
		//后置增强
		afterInvoke(arg1,arg2);
		return rtn;
	}
	
	/**
	 * 前置增强方法
	 * @param arg1
	 * @param arg2
	 * @throws Throwable
	 */
	private void beforeInvoke(Method arg1,Object[] arg2)throws Throwable{
		Method[] methods=aspectClass.getClass().getDeclaredMethods();
		for(Method m : methods){
			if(m.isAnnotationPresent(Before.class)){
				String pointcutAnnotation = ((Before)m.getAnnotation(Before.class)).annotation();
				Class pointcutAnnotationClazz=Class.forName(pointcutAnnotation);
				if(arg1.isAnnotationPresent(pointcutAnnotationClazz)){
					m.invoke(aspectClass, null);
				}
			}
		}
	}
	
	/**
	 * 后置增强方法
	 * @param arg1
	 * @param arg2
	 * @throws Throwable
	 */
	private void afterInvoke(Method arg1,Object[] arg2)throws Throwable{
		Method[] methods=aspectClass.getClass().getDeclaredMethods();
		for(Method m : methods){
			if(m.isAnnotationPresent(After.class)){
				String pointcutAnnotation = ((After)m.getAnnotation(After.class)).annotation();
				Class pointcutAnnotationClazz=Class.forName(pointcutAnnotation);
				if(arg1.isAnnotationPresent(pointcutAnnotationClazz)){
					m.invoke(aspectClass, null);
				}
			}
		}
	}

}
