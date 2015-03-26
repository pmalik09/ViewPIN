/*
 * Created on 4 Oct 2007
 *
 *
 */
package com.safenetinc.viewpin.testclient;

/**
 * Simple 'Bean' class to hold the results of a test call to the ViewPIN PINAgent
 * @author Paul Hampton
 *
 */
public class ViewPINTestResult
{
    private long pinRetrievalRequestTime;
    private long pinRetrievalResponseTime;
    
    /**
     * Standard constructor
     */
    public ViewPINTestResult()
    {
    }

    
    /**
     * @param pinRetrievalRequestTime the pinRetrievalRequestTime to set
     */
    public void setPinRetrievalRequestTime (long pinRetrievalRequestTime)
    {
        this.pinRetrievalRequestTime = pinRetrievalRequestTime;
    }

    /**
     * @param pinRetrievalResponseTime the pinRetrievalResponseTime to set
     */
    public void setPinRetrievalResponseTime (long pinRetrievalResponseTime)
    {
        this.pinRetrievalResponseTime = pinRetrievalResponseTime;
    }
    
    /**
     * @return the pinRetrievalRequestTime
     */
    public long getPinRetrievalRequestTime ()
    {
        return pinRetrievalRequestTime;
    }


    /**
     * @return the pinRetrievalResponseTime
     */
    public long getPinRetrievalResponseTime ()
    {
        return pinRetrievalResponseTime;
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer("PinRetrievalRequest: ");
        sb.append(pinRetrievalRequestTime);
        sb.append("\t PinRetrievalResponse: ");
        sb.append(pinRetrievalResponseTime);
        return sb.toString();
    }
}
