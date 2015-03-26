/*
 * Created on 26 Jul 2007
 * 
 * 
 */
package com.safenetinc.luna.virtualhosts;

import java.util.Vector;

import com.safenetinc.luna.virtualhosts.exception.ServerXmlException;

import com.safenetinc.Common;

/**
 * Java program/Luna SP command that lists the virtual hosts configured within a Tomcat install on a Luna SP
 * 
 * @author Paul Hampton
 */
public class ListVirtualHosts
{
    /**
     * Private constructor - only one static method for this class
     */
    private ListVirtualHosts()
    {
        //Private - nothing required here
    }

    /**
     * Prints the virtual hosts configured within a Luna SP to System.out
     * 
     */
    public static void getListOfHosts ()
    {
        Vector<String> hosts = null;
        try
        {
            hosts = ServerXmlHandler.getVirtualHostsList();
        }
        catch (ServerXmlException e)
        {
            System.err.println("Unable to obtain list of hosts");
            e.printStackTrace();
            return;
        }

        System.out.println("The following hosts were found");
        for (int i = 0; i < hosts.size(); i++)
        {
            System.out.println(hosts.get(i));
        }

    }

    /**
     * Invokes the ListVirtualHosts application by invoking the {@link #getListOfHosts()} method
     * @param args command line arguments - not used by this program
     */
    public static void main (String[] args)
    {
		if(args.length != 0)
		{
			System.out.println("ListVirtualHosts,no optional arguments required to list virtual hosts");
			return;
		}
		
		try
		{
			ListVirtualHosts.getListOfHosts();
		}
		catch(ClassCastException e)
		{
			System.out.println("Could not cast the class");
			System.out.println("ListVirtualHosts failed");
			return;
		}
		catch(NullPointerException e)
		{
			System.out.println("Passed values null not accepted");
			System.out.println("ListVirtualHosts failed");
			return;
		}
    }

}
