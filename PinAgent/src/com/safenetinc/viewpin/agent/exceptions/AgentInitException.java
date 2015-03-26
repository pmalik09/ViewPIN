// $Id: PinAgent/src/com/safenetinc/viewpin/agent/exceptions/AgentInitException.java 1.1 2008/09/04 10:46:03IST Mkhurana Exp  $
package com.safenetinc.viewpin.agent.exceptions;

/**
 * Exception to be thrown in event of a problem initialising the agent
 * 
 * @author Stuart Horler
 *
 *
 */
public class AgentInitException extends Exception
{
   private static final long serialVersionUID = 42L;

   /**
    * Constructor
    */
	public AgentInitException()
    {
    	super();
    }
    
	/**
	 * Constructor
	 * @param message The message associated with this exception
	 */
    public AgentInitException(String message)
    {
    	super(message);
    }
}
