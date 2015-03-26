// $Id: PinAgent/src/com/safenetinc/viewpin/common/utils/CookieUtils.java 1.2 2012/01/24 15:05:40IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author  Paul Hampton
 */
public class CookieUtils
{
    
    /** Creates a new instance of CookieUtils */
    private CookieUtils ()
    {
        //No action required here - private constructor
    }
    
    /**
     * Creates a new cookie to hold a piece of data
     * @param name The name of the cookie
     * @param data The data the cookie should contain
     * @param domain The domain of the cookie
     * @param path The path value for the cookie
     * @return The Cookie object
     */
    public static Cookie createCookieForData (String name, String data, String domain, String path)
    {
        if(name == null || data == null || domain == null || path == null)
        {
            throw new IllegalArgumentException ("null value passed to createCookieForData()");
        }
        
        Cookie cookie = new Cookie (name, data);
        
        cookie.setDomain(domain);
        cookie.setSecure(true);
        cookie.setMaxAge(-1);
        cookie.setPath(path);
		cookie.setVersion(1);
        
        return cookie;
    }
    
    /**
     * Method to obtain a cookie which can be used to delete another cookie
     * @param name The name of the cookie
     * @param domain The domain for the cookie
     * @param path The path of the cookie
     * @return The cookie to use in overwriting the original
     */
    public static Cookie deleteCookieByName (String name, String domain, String path)
    {
        if(name == null || domain == null)
        {
            throw new IllegalArgumentException ("null value passed to deleteCookieByName()");
        }
        
        Cookie cookie = new Cookie (name, "");
        
        cookie.setDomain (domain);
        cookie.setSecure (true);
        cookie.setMaxAge (0);
        cookie.setPath (path);
        
        return cookie;
    }
    
    /**
     * Method to retrieve a cookie by name.
     * @param request The request to retrieve the cookie from
     * @param cookieName The name of the cookie to retrieve
     * @return The cookie with the associated name, or null if no such cookie exists
     */
    public static Cookie getCookieByName (HttpServletRequest request, String cookieName)
    {
        if(request == null || cookieName ==null)
        {
            throw new IllegalArgumentException ("null value passed to getCookieByName()");
        }
        
        Cookie[] cookies = request.getCookies ();
        if(cookies == null)//if there are no cookies in the request then return null
            return null;
        
        Cookie returnCookie = null;
        
        for(int i =0; i<cookies.length;i++)
        {
            if(cookies[i].getName ().equals (cookieName))
            {
                returnCookie = cookies[i];
            }
        }

        return returnCookie;
    }
 }