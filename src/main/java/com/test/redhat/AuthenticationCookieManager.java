package com.test.redhat;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AuthenticationCookieManager
{
    public static String CREATION_TIMESTAMP_COOKIE     = "_creationTimestampCookie";
    public static String INACTIVITY_TIMEOUT_COOKIE     = "_inactityTimeoutCookie";
    public static String LAST_LOGON_TIMESTAMP_COOKIE   = "_lastLogonTimestampCookie";

    private static String COOKIE_NAME                  = 
                                                           "frameworkSecurityCookieName";

    private static String COOKIE_DOMAIN                = 
                                                           "frameworkSecurityCookieDomain";

    private static String COOKIE_PATH                  = 
                                                           "frameworkSecurityCookiePath";

    private static String COOKIE_TIMEOUT               = 
                                                           "10000";


   /**
    */
   public static Cookie getAuthCookie(HttpServletRequest req, HttpServletResponse res)
   {
       Cookie authCookie = getCookie(COOKIE_NAME, req);
       Cookie timeoutCookie = getCookie(COOKIE_NAME + INACTIVITY_TIMEOUT_COOKIE, req);
        
       if (authCookie != null && timeoutCookie != null)
       {
           long lastAccessTime = Long.parseLong(timeoutCookie.getValue());
           boolean authCookieExpired = (System.currentTimeMillis() - lastAccessTime) >
                                       (Integer.parseInt(COOKIE_TIMEOUT) * 60 * 1000);

           if (!authCookieExpired)
           {
        	    setCookie(COOKIE_NAME + INACTIVITY_TIMEOUT_COOKIE,
                         String.valueOf(System.currentTimeMillis()), req,res);

               return authCookie;
           }
           else {
               deleteAuthCookie(res);
           }
       }

       return null;
   }
   
   /**
    */
   public static Cookie getAuthCookie(HttpServletRequest req)
   {
       Cookie authCookie = getCookie(COOKIE_NAME, req);
       return authCookie;
   }   

   /**
    */
   public static long getAuthCookieCreationTime(HttpServletRequest req)
   {
       Cookie creationTimeCookie = getCookie(COOKIE_NAME + CREATION_TIMESTAMP_COOKIE, req);

       if (creationTimeCookie != null) {
           return Long.parseLong(creationTimeCookie.getValue());
       }
       else {
           return 0L;
       }
   }

   /**
    */
   public static String getLastLogonTimestamp(HttpServletRequest req)
   {
       Cookie lastLogonTS = getCookie(COOKIE_NAME + LAST_LOGON_TIMESTAMP_COOKIE, req);
       return (lastLogonTS != null) ? lastLogonTS.getValue() : null;
   }

    /**
     */
    public static void setAuthCookie(String userName, String lastLogonTS,HttpServletRequest req,
                                     HttpServletResponse res)
    {
        String currentTime = String.valueOf(System.currentTimeMillis());

        setCookie(COOKIE_NAME, userName,req, res);
        setCookie(COOKIE_NAME + INACTIVITY_TIMEOUT_COOKIE, currentTime, req,res);
        setCookie(COOKIE_NAME + CREATION_TIMESTAMP_COOKIE, currentTime, req,res);
        setCookie(COOKIE_NAME + LAST_LOGON_TIMESTAMP_COOKIE, lastLogonTS,req, res);
    }

    /**
     */
    public static void deleteAuthCookie(HttpServletResponse res)
    {
        deleteCookie(COOKIE_NAME, res);
        deleteCookie(COOKIE_NAME + INACTIVITY_TIMEOUT_COOKIE, res);
        deleteCookie(COOKIE_NAME + CREATION_TIMESTAMP_COOKIE, res);
        deleteCookie(COOKIE_NAME + LAST_LOGON_TIMESTAMP_COOKIE, res);
    }

    /**
     */
    protected static Cookie getCookie(String name, HttpServletRequest req)
    {
        Cookie[] cookies = req.getCookies();

        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                if (cookies[i].getName().equals(name)) {
                    return cookies[i];
                }
            }
        }

        return null;
    }

    /**
     */
    protected static void setCookie(String name, String value, HttpServletRequest req,HttpServletResponse res)
    {
        Cookie cookie = new Cookie(name, value);
        
        boolean secure=false;
        secure=req.isSecure();       //indicates whether this request was made using a secure channel, such as HTTPS.
        
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(-1);
        if(secure){
        	cookie.setSecure(true);
        }
       
        res.addCookie(cookie);
       // res.setHeader( "Set-Cookie", "name=value; HttpOnly");
    }

    /**
     */
    protected static void deleteCookie(String name, HttpServletResponse res)
    {
        Cookie cookie = new Cookie(name, null);

        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);

        res.addCookie(cookie);
    }

}
