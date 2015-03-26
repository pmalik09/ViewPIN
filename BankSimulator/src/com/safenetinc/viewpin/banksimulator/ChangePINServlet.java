/*
 * Created on Aug 2, 2005
 * 
 * 
 */
package com.safenetinc.viewpin.banksimulator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import com.safenetinc.viewpin.banksimulator.config.BankSimulatorCustomerConfiguration;
import com.safenetinc.viewpin.banksimulator.config.BankSimulatorUser;
import com.safenetinc.viewpin.authority.CardHolderDetails;

/**
 * Servlet to emulate logging into our pretend bank
 * 
 * @author Paul Hampton
 */
public class ChangePINServlet extends HttpServlet
{
    /** Standard serialization UID */
    public static final long    serialVersionUID   = 001;

    private static final String ID_PARAMETER       = "id";

    private static final String CHANGE_PARAMETER = "changeid";

	private static final String PASSWORD_PARAMETER = "password";

    /**User id constant*/
    public static final String  USER_PARAMETER     = "user";

    private XMLConfiguration    config             = new XMLConfiguration();
    
    private static Logger logger = Logger.getLogger(SelectPINServlet.class);
    
    /**
     * Handles a customer/user changePIN
     */
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        getLogger().debug("ChangePINServlet v1 up and running");
		int changeid =0;
		changeid = Integer.parseInt(request.getParameter(CHANGE_PARAMETER));

		getLogger().debug("changeid is: " + changeid);
        // First get the two login parameters
        //String id = request.getParameter(ID_PARAMETER);
        //String password = request.getParameter(PASSWORD_PARAMETER);
		// request.getSession().getAttribute(USER_PARAMETER, user);
        // Validate the parameters here...
		/*
        if (id == null)
        {
            getLogger().warn("ID is null");
            response.sendRedirect(this.config.getString("LoginPage(0)"));
            return;
        }
        if (id.equals(""))
        {
        	getLogger().warn("id is blank");
            response.sendRedirect(this.config.getString("LoginPage(0)"));
            return;
        }

        if (password == null)
        {
        	getLogger().warn("password is null");
            response.sendRedirect(this.config.getString("LoginPage(0)"));
            return;
        }
        if (password.equals(""))
        {
        	getLogger().warn("password is blank");
            response.sendRedirect(this.config.getString("LoginPage(0)"));
            return;
        }
*/
        // First check that the simulator has been configured
        BankSimulatorCustomerConfiguration configuration = (BankSimulatorCustomerConfiguration) getServletContext().getAttribute(BankSimulatorServlet.CUSTOMER_CONFIGURATION_PARAMETER);

        if (configuration == null)
        {
        	getLogger().error("BankSimulatorServlet has not been configured - cannot log in. Redirecting to error page");
            response.sendRedirect("/needsconfiguration.html");
            return;
        }

      //  BankSimulatorUser user = configuration.findUseronID(id);
		BankSimulatorUser user = (BankSimulatorUser) request.getSession().getAttribute(USER_PARAMETER);
        if (user == null)
        {
        	getLogger().warn("customer not found in current configuration");
            response.sendRedirect(this.config.getString("LoginPage(0)"));
            return;
        }
		else
		{
			getLogger().debug("found this user");
		}
        
       
        request.getSession().setAttribute("PINID", changeid);
		getLogger().debug("request.getSession().getAttribute: " + request.getSession().getAttribute("PINID"));
   
        request.getSession().setAttribute("USER_PARAMETER", user);

    

        try
        {
            getServletConfig().getServletContext().getRequestDispatcher("/changePIN.jsp").forward(request, response);
        }
        catch (Exception ioe)
        {
            getLogger().error("IOException reporting success", ioe);
        }
       // response.sendRedirect(this.config.getString("changePIN(0)"));

    }

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException
    {
        URL configurationFile = Thread.currentThread().getContextClassLoader().getResource("/BankSimulatorConfig.xml");
        
        getLogger().debug("configuration file = " + configurationFile);
        
        try
        {
        	// Open our configuration file
            this.config.load(configurationFile);
        }
        catch(ConfigurationException ce)
        {
        	getLogger().fatal("loading configuration file " + ce.getMessage());
        	
        	throw new ServletException();
        }
    }

    private static Logger getLogger()
    {
        return logger;
    }
}
