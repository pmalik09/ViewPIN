/*
 * Created on Aug 11, 2005
 * 
 * 
 */
package com.safenetinc.viewpin.banksimulator;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import com.safenetinc.viewpin.banksimulator.config.BankSimulatorCustomerConfiguration;

/**
 * Servlet to display the current configuration status of the Bank Simulator
 * @author Paul Hampton
 */
public class ViewConfigurationServlet extends HttpServlet
{
    /** Standard serialization UID */
    public static final long serialVersionUID = 001;

    private XMLConfiguration config           = new XMLConfiguration();

    private static Logger logger = Logger.getLogger(LoginServlet.class);
    
    /**
     * Retrieves the configuration from the {@link HttpSession} and forwards it to a jsp for display 
     */
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        getLogger().debug("ViewConfiguration servlet up and running");

        BankSimulatorCustomerConfiguration configuration = (BankSimulatorCustomerConfiguration) getServletContext().getAttribute(BankSimulatorServlet.CUSTOMER_CONFIGURATION_PARAMETER);
        if (configuration == null)
        {
            getLogger().error("Bank simulator not configured; redirecting");
            response.sendRedirect(this.config.getString("ConfigurationPage(0)"));
            return;
        }
        if (configuration.getUsers().size() == 0)
        {
            getLogger().error("Bank simulator has no users - redirecting");
            response.sendRedirect(this.config.getString("ConfigurationPage(0)"));
            return;
        }

        request.getSession().setAttribute(BankSimulatorServlet.CUSTOMER_CONFIGURATION_PARAMETER, configuration);

        try
        {
            getServletConfig().getServletContext().getRequestDispatcher("/viewconfig.jsp").forward(request, response);
        }
        catch (Exception ioe)
        {
            getLogger().error("IOException forwarding user jsp", ioe);
            ioe.printStackTrace();
        }
    }

    /**
     * Loads the Bank Simulator's configuration file
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init () throws ServletException
    {
        // Open our configuration file
        File configurationFile = new File(this.getServletContext().getRealPath(this.getServletName()));

        try
        {
            this.config.load(configurationFile.getParent() + "/BankSimulatorConfig.xml");
        }
        catch (ConfigurationException e)
        {
            getLogger().error("Unable to load BankSimulatorConfig.xml configuration file");
            e.printStackTrace();
        }
    }

    private static Logger getLogger()
    {
        return logger;
    }
}
