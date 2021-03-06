package com.salesforce.web;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.salesforce.SalesForceEnvironment;
import com.salesforce.implement.Action;
import com.salesforce.implement.ActionFactoryImpl;

/**
 * Servlet implementation class ExternalInterfaceServlet
 */
@WebServlet(description = "External Interface Servlet of request", urlPatterns = { "/es" })
public class ExternalInterfaceServlet extends HttpServletJXGB implements Servlet {
	private static final long serialVersionUID = 1L;
    private ServletContext context = null;
    private String classname = ExternalInterfaceServlet.class.getSimpleName();  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExternalInterfaceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			if(SalesForceEnvironment.getInstance()==null)
				SalesForceEnvironment.init();
			} catch (Exception e) {
			System.out.println(" ************** CRITICAL ERROR : Failed to initialize core Environment parameters *************");	
			
		}
		super.init(config);	
		}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
			super.destroy(); // destroy the Connection Pool
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action =null;
		try{
			this.context = (ServletContext)getServletContext();
			if(request.getParameter("qs")!=null){
				action = request.getParameter("qs").trim();
			}else{
				action = "default";
			}
			SalesForceEnvironment.setComment(3,classname," method service : action = "+action+" and rules is "+ request.getParameter("rules")); //remove it after sometime 
				if((delegateAction(action, request, response, context))==false){
					throw new Exception (classname+" CRITICAL ERROR: delegateAction failed...");
				}
			
		}catch(Exception e){
			SalesForceEnvironment.setComment(1,classname," method service : Problem with delegate action.. action = "+action );
		}
	}
	private boolean delegateAction(String action, HttpServletRequest request, HttpServletResponse response, ServletContext context) throws Exception
    {
		 Action actionRef = null;
	       boolean result = false;
	       if(action != null) {
	       try {
	    	   actionRef = new ActionFactoryImpl().createAction(action); 
	                 request.setAttribute("lastaction", action);
	                   actionRef.perform(action,request, response, context);
	                   result=true;
	       
	       	}catch(Exception ee){
				result = false;
				SalesForceEnvironment.setComment(3,classname," Exception is  "+ee.getMessage()  );
				}
	       }else{
	    	   throw new Exception (classname+" Exception in delegateAction, action is "+action);       
	    	   }   	   
			return result;

    }
	
}
