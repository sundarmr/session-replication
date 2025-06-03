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

    static Logger logger = Logger.getLogger(SingleSignonFilter.class);
    private FilterConfig config = null;


    /**
     */
    public void init(FilterConfig config) throws ServletException
    {
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
        if (isDisabled())
        {
            
            chain.doFilter(request, response);
            return;
        }
        // START - FIX FOR FILTER NOT LOADING
        String requestURI = ((HttpServletRequest) request).getRequestURI();
		String contextPath = request.getServletContext().getContextPath() + "/views";
		if (!requestURI.startsWith(contextPath)) {
			chain.doFilter(request, response);
			return;
		}
        // END - FIX FOR FILTER NOT LOADING
		
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        Cookie authCookie = AuthenticationCookieManager.getAuthCookie(req, res);

        if (authCookie != null)
        {
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

        // When the request is not authenticated, redirect to the login page.
        
        String uri = "/logon/logon.jsp";
        if(request.getParameter("loginid") != null){
        	uri = uri + "?loginid=" + request.getParameter("loginid");
        }
        
        RequestDispatcher dispatcher = config.getServletContext().getContext("/framework").getRequestDispatcher(uri);
        
        
        req.setAttribute("originalRequestUri", req.getRequestURI());
        req.setAttribute("originalRequestQueryparameters", req.getQueryString());
        dispatcher.forward(req, res);
        
    }

    //	----------------------------------------------------------------------
    

}


