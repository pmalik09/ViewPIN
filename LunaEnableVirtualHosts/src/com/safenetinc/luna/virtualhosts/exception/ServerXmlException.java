/*
 * Created on Jul 17, 2007
 * 
 * 
 */
package com.safenetinc.luna.virtualhosts.exception;

/**
 * Simple exception class to represent exceptions occuring during the editing of a tomcat server.xml file.
 * 
 * @author Paul Hampton
 */
public class ServerXmlException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for the exception. 
     * @param message The message accompanying the exception
     */
    public ServerXmlException(String message)
    {
        super(message);
    }

}
