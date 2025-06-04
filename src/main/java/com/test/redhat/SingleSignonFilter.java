package com.test.redhat;



import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;







public class SingleSignonFilter implements Filter
{
    public static final String ACL_PATH                    	= "/framework/logon/logon.jsp";
    public static final String AUTH_USER_SESSION_KEY       	= "authenticatedUserObject";
    
    //public static final String NEW_USER_SESSION_KEY 		= "NewauthenticatedUserObject";

    public static final String SINGLE_SIGN_ON_DISABLED =
        "com.ast.framework.security.SingleSignon.disable";

    
    private FilterConfig config = null;


    /**
     */
    public void init(FilterConfig config) throws ServletException
    {
	    this.config =config;
       System.out.println("Initialized.....");
    }

    /**
     */
    public void destroy()
    {
        config = null;
    }

    /**
     */
    public static boolean isDisabled()
    {
    	/***********************************************
    	 * AST Prototype change
    	 */
        //return (Boolean.getBoolean(SINGLE_SIGN_ON_DISABLED));
    	return false;
    }

    /**
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
        IOException, ServletException
    {
       System.out.println("In Do Filter");
       
		
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        Cookie authCookie = AuthenticationCookieManager.getAuthCookie(req, res);

        if (authCookie != null)
        {
		System.out.println("Auth cookie is not null");
            long cookieCreationTime = AuthenticationCookieManager.getAuthCookieCreationTime(req);
            HttpSession session = req.getSession();

            if (session.getCreationTime() < cookieCreationTime)
            {
                session.invalidate();
                session = req.getSession(true);
            }

            String user = (String) session.getAttribute(AUTH_USER_SESSION_KEY);
            if (user == null)
            {
                String lastLogonTS = AuthenticationCookieManager.getLastLogonTimestamp(req);

                user = authCookie.getValue();
                session.setAttribute(AUTH_USER_SESSION_KEY, user);
                //session.setAttribute(NEW_USER_SESSION_KEY, user);
            }

            chain.doFilter(request, response);
            return;
        }

         else {
        	 System.out.println("Cookie is not present");
		boolean islogin = req.getRequestURI().contains("login");
		 if(!islogin){
        	     String uri = "/login.jsp";
                     System.out.println("Config is ..."+config);
                     res.sendRedirect(uri);
		 }
		 chain.doFilter(request, response);
            return;
        }
        
    }

    //	----------------------------------------------------------------------
    

}


