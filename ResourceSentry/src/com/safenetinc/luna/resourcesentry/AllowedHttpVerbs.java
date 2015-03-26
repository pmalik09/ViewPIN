// $Id: ResourceSentry/src/com/safenetinc/luna/resourcesentry/AllowedHttpVerbs.java 1.1 2008/09/04 10:52:14IST Mkhurana Exp  $
package com.safenetinc.luna.resourcesentry;

class AllowedHttpVerbs
{
    private boolean getAllowed  = false;

    private boolean postAllowed = false;

    /**
     * @param getAllowed Are GET requests allowed?
     * @param postAllowed Are POST requests allowed?
     */
    public AllowedHttpVerbs(boolean getAllowed, boolean postAllowed)
    {
        super();

        setGetAllowed(getAllowed);
        setPostAllowed(postAllowed);
    }

    private void setGetAllowed (boolean getAllowed)
    {
        this.getAllowed = getAllowed;
    }

    /**
     * @return boolean denoting if GET requests are allowed
     */
    public boolean isGetAllowed ()
    {
        return this.getAllowed;
    }

    private void setPostAllowed (boolean postAllowed)
    {
        this.postAllowed = postAllowed;
    }

    /**
     * @return boolean denoting if POST requests are allowed
     */
    public boolean isPostAllowed ()
    {
        return this.postAllowed;
    }
}
