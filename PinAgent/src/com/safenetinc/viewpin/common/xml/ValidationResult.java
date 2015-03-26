// $Id: PinAgent/src/com/safenetinc/viewpin/common/xml/ValidationResult.java 1.1 2008/09/04 10:47:47IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class to represent the result of a validation
 * 
 * @author Stuart Horler
 *
 *
 */
public class ValidationResult implements ErrorHandler
{
    private boolean valid = true;
    private SAXParseException exception = null; 
    
    /**
     * Constructor
     */
    public ValidationResult()
    {
        super();
    }
    
    /**
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) throws SAXException
    {
        setValid(false);
        
        setException(e);
    }

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException
    {
        setValid(false);
        
        setException(e);
    }
     
    /**
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) throws SAXException
    {
        setValid(false);
        
        setException(e);
    }

    /**
     * @return boolean denoting whether the result is valid
     */
    public boolean isValid() 
    {
        return this.valid;
    }

    private void setValid(boolean valid) 
    {
        this.valid = valid;
    }
    
    /**
     * Resets the validation result
     */
    public void reset()
    {
        setValid(true);
    }

    private void setException(SAXParseException exception) 
    {
        this.exception = exception;
    }
    
    /**
     * 
     * @return a {@link SAXParseException} that may have been generated during validation
     */
    public SAXParseException getException() 
    {
        return this.exception;
    }
}