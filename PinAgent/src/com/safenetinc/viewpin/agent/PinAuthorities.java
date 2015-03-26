// $Id: PinAgent/src/com/safenetinc/viewpin/agent/PinAuthorities.java 1.2 2012/07/30 13:49:28IST Malik, Pratibha (Pmalik) Exp  $
package com.safenetinc.viewpin.agent;

import java.util.ArrayList;

import com.safenetinc.viewpin.common.datastructures.SubjectKeyIdentifier;

/**
 * Class to hold a list of PIN Authorities.
 * @author Stuart Horler
 *
 */
public class PinAuthorities
{
    private ArrayList<PinAuthority> pinAuthorities = null;
    
    PinAuthorities()
    {
        super();
    
        setPinAuthorities(new ArrayList<PinAuthority>());
    }
    
    void add(PinAuthority pinAuthority)
    {
        getPinAuthorities().add(pinAuthority);
    }
    
    private void setPinAuthorities(ArrayList<PinAuthority> pinAuthorities)
    {
        this.pinAuthorities = pinAuthorities;
    }
    
    private ArrayList<PinAuthority> getPinAuthorities()
    {
        return this.pinAuthorities;
    }
    
    /**
     * Returns the PINAuthority associated with the supplied signing SKI
     * @param subjectKeyIdentifier The SKI to lookup the PINAuthority from
     * @return The PINAuthority associated with the SKI
     */
    public PinAuthority getBySigning(SubjectKeyIdentifier subjectKeyIdentifier)
    {
        PinAuthority pinAuthority;
        PinAuthority nextPinAuthority;
        
        pinAuthority = null;
        nextPinAuthority = null;
        
        for(int i = 0; i < getPinAuthorities().size(); i++)
        {
            nextPinAuthority = getPinAuthorities().get(i);
            
            if(nextPinAuthority.getSigningCertificateSubjectKeyIdentifier().equals(subjectKeyIdentifier) == true)
            {
                pinAuthority = nextPinAuthority;
                
                break;
            }
        }
        
        return pinAuthority;
    }
    
    /**
     * Returns the PINAuthority associated with the supplied wrapping SKI
     * @param subjectKeyIdentifier The SKI to lookup the PINAuthority from
     * @return The PINAuthority associated with the SKI
     */
    public PinAuthority getByWrapping(SubjectKeyIdentifier subjectKeyIdentifier)
    {
        PinAuthority pinAuthority;
        PinAuthority nextPinAuthority;
        
        pinAuthority = null;
        nextPinAuthority = null;
        
        for(int i = 0; i < getPinAuthorities().size(); i++)
        {
            nextPinAuthority = getPinAuthorities().get(i);
            
            if(nextPinAuthority.getWrappingCertificateSubjectKeyIdentifier().equals(subjectKeyIdentifier) == true)
            {
                pinAuthority = nextPinAuthority;
                
                break;
            }
        }
        
        return pinAuthority;
    }
    /**
     * Returns the PINAuthority associated with the supplied Name
     * @param Name The Name to lookup the PINAuthority from
     * @return The PINAuthority associated with the Name
     */
    public PinAuthority getByName(String authorityName)
    {
        PinAuthority pinAuthority;
        PinAuthority nextPinAuthority;
        
        pinAuthority = null;
        nextPinAuthority = null;
        
        for(int i = 0; i < getPinAuthorities().size(); i++)
        {
            nextPinAuthority = getPinAuthorities().get(i);
            
            if(nextPinAuthority.getAuthorityName().compareToIgnoreCase(authorityName) == 0)
            {
                pinAuthority = nextPinAuthority;
                
                break;
            }
        }
        
        return pinAuthority;
    }
    /**
     * @return The default (first in the list) PINAuthority to use
     */
    public PinAuthority getDefault()
    {
        PinAuthority pinAuthority;
        
        pinAuthority = null;
        
        if(getPinAuthorities().size() > 0)
        {
            pinAuthority = getPinAuthorities().get(0);
        }
        
        return pinAuthority;
    }
}