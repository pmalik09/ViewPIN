package com.safenetinc.viewpin.banksimulator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to handle invalidating the current user's session.
 * 
 */
public class LogoutServlet extends HttpServlet 
{
    private static final long serialVersionUID = 42L;

	/**
	 * Initialise the Servlet
	 */
	@Override
	public void init() throws ServletException
    {
		super.init();
    }
	
	/**
	 * Handles a post to this servlet, invalidating the current session.
	 * @param request The Servlet request
	 * @param response The Servlet response
	 */
	@Override
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	    request.getSession().invalidate();
    }

	/**
     * Handles a get to this servlet, invalidating the current session.
     * @param request The Servlet request
     * @param response The Servlet response
     */
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        request.getSession().invalidate();
    }
	
	
}
