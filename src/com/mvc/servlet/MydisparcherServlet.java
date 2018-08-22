package com.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mvc.annotation.aop.MyAspect;
import com.mvc.annotation.ioc.MyService;
import com.mvc.annotation.ioc.Myautowrited;
import com.mvc.annotation.mvc.Mycontroller;
import com.mvc.annotation.mvc.Myparamer;
import com.mvc.annotation.mvc.RequestMapping;
import com.mvc.proxy.SimpleProxyFactory;


/**
 * Servlet implementation class MydisparcherServlet
 */

public class MydisparcherServlet extends HttpServlet {
	public static Logger log = Logger.getLogger(MydisparcherServlet.class.toString());
	
	private static final long serialVersionUID = 1L;
	
	private static ServletContext ctx ;
	
	private static Map<String,Object> Ioc=new HashMap<String,Object>();
	
	private static Map<String,Handle> HandleMapping=new HashMap<String,Handle>();
	
	private static List<String> classnamelist= new ArrayList<String>();
	
	private static List<Object> aspectClassList= new ArrayList<Object>();
	
	private Properties properties=new Properties();
	
	private InputStream in;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MydisparcherServlet() {
        super();
        
    }
    static{
    	ConsoleHandler fileHandler = new ConsoleHandler();  
         fileHandler.setLevel(Level.ALL);  
        // fileHandler.setFormatter(new Formatter());  
         log.addHandler(fileHandler);  
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @throws IOException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//寻找匹配的controller及方法
	    try {
			dodispath(request,response);
		} catch (Exception e) {
			
			e.printStackTrace();		
			response.getWriter().write("500 : error");
		}
	}

	
	
	private void dodispath(HttpServletRequest request, HttpServletResponse response) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String url=request.getPathInfo();
		Handle h=HandleMapping.get(url);
		if(null==h){
			response.getWriter().write("404 : NOT FOUND");
			return;
		}
		Map<String, String[]> map=request.getParameterMap();
		
		Object[] os=new Object[h.getParamap().size()];
		 Map map1=h.getParamap();
		for(String key:map.keySet()){
			if( h.getParamap().containsKey(key)){
				os[(int) h.getParamap().get(key)]=map.get(key)[0];
			}
			
		}
		os[(int) h.getParamap().get(HttpServletRequest.class.getName())]=request;
		os[(int) h.getParamap().get(HttpServletResponse.class.getName())]=response;
		h.getM().invoke(Ioc.get(LowerFirstchar(h.getController().getSimpleName())), os);
	}

	@Override
	public void init() throws ServletException {
		
		//super.init();
		//加载配置文件
		doloading();
		
		//包扫描
		pacagescan(properties.getProperty("package.scan"));
		
		//初始化spring容器		
		initIoc();
		
		
		//自动注入
		aotuwirted();
		
		//初始化handlemapping
		initHandleMapping();
				
		
	}

	private void initHandleMapping() {
		for(Map.Entry<String, Object> entry:Ioc.entrySet()){
			Class clazz = entry.getValue().getClass();
			if(!clazz.isAnnotationPresent(Mycontroller.class)){
				continue;
			}
			String base="";
			if(clazz.isAnnotationPresent(RequestMapping.class)){
				RequestMapping re=(RequestMapping) clazz.getAnnotation(RequestMapping.class);
				base=re.value();
			}
			Method[] methods=clazz.getDeclaredMethods();
			for(Method m:methods){
				if(!m.isAnnotationPresent(RequestMapping.class)){
					continue;
				}
				RequestMapping res=(RequestMapping) m.getAnnotation(RequestMapping.class);
				
				Parameter[] prs=m.getParameters();
				
				Map paramaerIndexMap=new HashMap();
				for(int i=0;i<prs.length;i++){
					Parameter p=prs[i];
					if(p.getType()==HttpServletRequest.class||p.getType()==HttpServletResponse.class){
						paramaerIndexMap.put(p.getType().getName(), i);
						continue;
					}
					String paraname="";
					if(p.isAnnotationPresent(Myparamer.class)){
						Myparamer mp=p.getAnnotation(Myparamer.class);
						paraname=mp.value();
					}else{
						paraname=p.getName();
					}
					paramaerIndexMap.put(paraname, i);
				}
				
				Handle h=new Handle(clazz,m,paramaerIndexMap);
				
				HandleMapping.put(base+res.value(), h);
								
			}
		}
		
	}

	private void doloading() {
		ctx = this.getServletContext();
		
		in = ctx.getResourceAsStream("WEB-INF\\classes\\mvc.properties");
				
		try {
			properties.load(in);
		} catch (IOException e) {
			
			log.info(e.getMessage());
		}finally{
			if(null!=in){
				try {
					in.close();
				} catch (IOException e) {
					
					log.info(e.getMessage());
				}
			}
		}
		
	}

	private void pacagescan(String packagescan) {
		//Assert.notNull(packagescan,"");
		URL url=null;
		try {
			url = ctx.getResource("WEB-INF\\classes\\"+packagescan.replace(".", "\\"));
			//System.out.println(url.getPath());
		} catch (MalformedURLException e) {
			
			log.info(e.getMessage());
		}
		
		File file=new File(url.getFile());
		
		for(File f:file.listFiles()){
			if(f.isDirectory()){
				pacagescan(packagescan+"."+f.getName());
			}else{
				String classname=(packagescan+"."+f.getName()).replaceAll(".class", "");
				
				classnamelist.add(classname);
			}
		}
		
	}

	private void aotuwirted() {
		
		for(String key:Ioc.keySet()){
			Object c=Ioc.get(key);
			Field[] fs=c.getClass().getDeclaredFields();
			for(Field f:fs){
				if(f.isAnnotationPresent(Myautowrited.class)){
					f.setAccessible(true);
					Myautowrited au=f.getAnnotation(Myautowrited.class);
					try {
					
						f.set(c, "".equals(au.value())?Ioc.get(LowerFirstchar(f.getType().getSimpleName())):Ioc.get(au.value()));
					} catch (IllegalArgumentException e) {
						log.info(e.getMessage());
					} catch (IllegalAccessException e) {
						log.info(e.getMessage());
					}
				}
			}
			Ioc.put(key, c);
		}
	}

	private void initIoc() throws ServletException  {
		
		//初始化IOC之前先初始化Aop,因为IOC容器存代理对象
		for(String classname:classnamelist){
			Class clazz;
			try {
				clazz = Class.forName(classname);
				
				if(clazz.isAnnotationPresent(MyAspect.class)){
					
					aspectClassList.add(clazz.newInstance());
				}
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		for(String classname:classnamelist){
			try {
				Class clazz=Class.forName(classname);
				if(clazz.isAnnotationPresent(Mycontroller.class)){
					
					Ioc.put(LowerFirstchar(clazz.getSimpleName()), clazz.newInstance());
				}
				
                 if(clazz.isAnnotationPresent(MyAspect.class)){
					
					Ioc.put(LowerFirstchar(clazz.getSimpleName()), clazz.newInstance());
				}
				if(clazz.isAnnotationPresent(MyService.class)){
					MyService s=(MyService) clazz.getAnnotation(MyService.class);
					String beanname;
					if("".equals(s.value())){						
						beanname=LowerFirstchar(clazz.getSimpleName());
					}else{
						beanname=s.value();
					}
					
					//Ioc.put(beanname, clazz.newInstance());
					
					if(clazz.isInterface()){
						Set<Class<?>> set= getAllSubclassByInterface(classnamelist,clazz);
						if(set.size()==0){
							throw new ServletException("one interface have no subclass");
						}
						if(set.size()>1){
							throw new ServletException("one interface have more than one subclass");
						}
						Ioc.put(beanname, SimpleProxyFactory.newInstance(set.iterator().next().newInstance(), aspectClassList.get(0)));
					}
					
				}
				
			} catch (ClassNotFoundException e) {
				
				log.info(e.getMessage());
			} catch (InstantiationException e) {
				
				log.info(e.getMessage());
			} catch (IllegalAccessException e) {
				
				log.info(e.getMessage());
			}
			
		}
		
	}

	private Set<Class<?>> getAllSubclassByInterface(List<String> classnamelist2, Class clazz) {
		Set<Class<?>> set=new HashSet<Class<?>>();
		for(String classname:classnamelist2){
			try {
				Class clz=Class.forName(classname);
				Class[] c=clz.getInterfaces();
				for(Class cz:c){
					if(cz==clazz){
						set.add(clz);
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				log.info(e.getMessage());
			}
		}
		return set;
	}

	private String LowerFirstchar(String classname) {
		char[] ch=classname.toCharArray();
		ch[0]+=32;
		return new String(ch);
	}

	class Handle {
		private Class<?> controller;

		private Method m;

		private Map paraIndexMap;

		public Handle(Class clazz, Method m2, Map paramaerIndexMap) {
			this.controller=clazz;
			this.m=m2;
			this.paraIndexMap=paramaerIndexMap;
		}

		public Class<?> getController() {
			return controller;
		}

		public void setController(Class<?> controller) {
			this.controller = controller;
		}

		public Method getM() {
			return m;
		}

		public void setM(Method m) {
			this.m = m;
		}

		public Map getParamap() {
			return paraIndexMap;
		}

		public void setParamap(Map paramap) {
			this.paraIndexMap = paramap;
		}

	}
}
