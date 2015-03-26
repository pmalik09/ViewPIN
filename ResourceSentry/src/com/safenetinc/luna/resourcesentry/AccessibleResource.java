// $Id: ResourceSentry/src/com/safenetinc/luna/resourcesentry/AccessibleResource.java 1.1 2008/09/04 10:52:12IST Mkhurana Exp  $
package com.safenetinc.luna.resourcesentry;

import java.util.Vector;

class AccessibleResource
{
    private String           name                      = null;

    private AllowedHttpVerbs allowedHttpVerbs          = null;

    private boolean          localOnly                 = true;

    private boolean          restrictByIPAddress       = false;

    private boolean          allowNonRoutableAddresses = false;

    private Vector<String>           allowedIPAddresses        = null;

    /**
     * Constructs an instance of this class with the supplied values.
     * 
     * @param name The name of the resource
     * @param allowedHttpVerbs The AllowedHttpVerbs for this resource
     * @param localOnly Denotes whether the resource is localhost accessible only
     * @param restrictByIPAddress Denotes whether the resource is only accessible from specified IP Addresses
     * @param allowNonRoutableAddresses Denotes whether the resource is only accessible from non-routable IP
     *        Addresses
     * @param allowedIPAddresses {@link Vector} The IP Addresses that the resource is accessible from
     */
    public AccessibleResource(String name, AllowedHttpVerbs allowedHttpVerbs, boolean localOnly, boolean restrictByIPAddress, boolean allowNonRoutableAddresses, Vector<String> allowedIPAddresses)
    {
        super();

        setName(name);
        setAllowedHttpVerbs(allowedHttpVerbs);
        setLocalOnly(localOnly);
        // Set whether or not we should allow only certain IP Addresses to use this resource
        setRestrictByIPAddress(restrictByIPAddress);
        // Set whether or not to allow all non-routable addresses
        setAllowNonRoutableAddresses(allowNonRoutableAddresses);
        // Set the vector of addresses to allow
        setAllowedIPAddresses(allowedIPAddresses);
    }

    /**
     * Sets the name parameter for this resource
     * 
     * @param name The name parameter
     */
    public void setName (String name)
    {
        this.name = name;
    }

    /**
     * Gets the name parameter for this resource
     * 
     * @return The name parameter
     */
    public String getName ()
    {
        return this.name;
    }

    /**
     * Sets the link AllowedHttpVerbs parameter for this resource
     * 
     * @param allowedHttpVerbs The AllowedHttpVerbs for this resource
     */
    public void setAllowedHttpVerbs (AllowedHttpVerbs allowedHttpVerbs)
    {
        this.allowedHttpVerbs = allowedHttpVerbs;
    }

    /**
     * Gets the AllowedHttpVerbs value for this resource
     * 
     * @return The AllowedHttpVerbs for this resource
     */
    public AllowedHttpVerbs getAllowedHttpVerbs ()
    {
        return this.allowedHttpVerbs;
    }

    /**
     * Sets the localOnly parameter for this resource. This denotes whether a resource should only be accessed
     * by localhost.
     * 
     * @param localOnly boolean denoting whether local access only should be enforced for this resource
     */
    public void setLocalOnly (boolean localOnly)
    {
        this.localOnly = localOnly;
    }

    /**
     * Gets the value of the localOnly parameter for this resource. This defines whether a resource should
     * only be accessed by localhost.
     * 
     * @return boolean denoting if access to the resource should be from localhost only
     */
    public boolean isLocalOnly ()
    {
        return this.localOnly;
    }

    /**
     * Gets the {@link Vector} that represents the IP addresses that are allowed to access this resource.
     * 
     * @return the allowedIPAddresses The {@link Vector} of IP addresses as {@link String} objects
     */
    public Vector<String> getAllowedIPAddresses ()
    {
        return this.allowedIPAddresses;
    }

    /**
     * Sets the {@link Vector} of addresses that are allowed to access this resource
     * 
     * @param allowedIPAddresses a {@link Vector} containing {@link String}s representing the IP addresses
     *        that are allowed to access this resource
     */
    public void setAllowedIPAddresses (Vector<String> allowedIPAddresses)
    {
        this.allowedIPAddresses = allowedIPAddresses;
    }

    /**
     * Denotes whether this resource should be accessed from non-routable IP addresses only
     * 
     * @return allowNonRoutableAddresses 
     */
    public boolean isAllowNonRoutableAddresses ()
    {
        return this.allowNonRoutableAddresses;
    }

    /**
     * Sets whether the resource can only be accessed from non-routable IP addresses
     * @param allowNonRoutableAddresses the allowNonRoutableAddresses to set
     */
    public void setAllowNonRoutableAddresses (boolean allowNonRoutableAddresses)
    {
        this.allowNonRoutableAddresses = allowNonRoutableAddresses;
    }

    /**
     * Denotes whether this resource should be accessed only from listed IP addresses
     * 
     * @return restrictByIPAddress 
     */
    public boolean isRestrictByIPAddress ()
    {
        return this.restrictByIPAddress;
    }

    /**
     * @param restrictByIPAddress the restrictByIPAddress to set
     */
    public void setRestrictByIPAddress (boolean restrictByIPAddress)
    {
        this.restrictByIPAddress = restrictByIPAddress;
    }
}
