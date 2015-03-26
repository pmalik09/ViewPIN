/*
 * Created on Aug 2, 2005
 * 
 * 
 */
package com.safenetinc.viewpin.banksimulator.config;

import com.safenetinc.viewpin.banksimulator.BankSimulatorUser;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to hold the current configuration state of BankSimulator customers. In a real banking environment
 * this would of course be some form of relational database
 * 
 * @author Paul Hampton
 */
public class BankSimulatorCustomerConfiguration implements Serializable
{
    private static final long            serialVersionUID = 1L;

    private ArrayList<BankSimulatorUser> users            = new ArrayList<BankSimulatorUser>();

    /**
     * Adds a customer/user to the current configuration state
     * @param user The user to add
     */
    public void addUser (BankSimulatorUser user)
    {
        this.users.add(user);
    }

    /**
     * Returns and {@link ArrayList} of all {@link BankSimulatorUser} objects currently held in the Configuration
     * @return Returns the users.
     */
    public ArrayList<BankSimulatorUser> getUsers ()
    {
        return this.users;
    }

    /**
     * Sets a new list of users
     * @param users An {@link ArrayList} of {@link BankSimulatorUser} user objects
     */
    public void setUsers (ArrayList<BankSimulatorUser> users)
    {
        this.users = users;
    }

    /**
     * Locates a user within the current configuration
     * @param id The ID of the user to find
     * @param password The password of the user
     * @return The corresponding {@link BankSimulatorUser} object or null if no match was found
     */
    public BankSimulatorUser findUser (String id, String password)
    {
        for (int i = 0; i < this.users.size(); i++)
        {
            BankSimulatorUser user = this.users.get(i);
            if (user.getId().equalsIgnoreCase(id) && user.getPassword().equals(password))
                return user;
        }

        return null;
    }
	
	public boolean ifUserExists (String id)
    {
        for (int i = 0; i < this.users.size(); i++)
        {
            BankSimulatorUser user = this.users.get(i);
            if (user.getId().equalsIgnoreCase(id))
                return true;
        }

        return false;
    }

}
