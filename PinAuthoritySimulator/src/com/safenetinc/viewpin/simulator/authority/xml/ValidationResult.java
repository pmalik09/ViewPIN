package com.safenetinc.viewpin.simulator.authority.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Class to store the result of a validation
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class ValidationResult implements ErrorHandler
{
    private boolean           valid     = true;

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
    public void error (SAXParseException e) throws SAXException
    {
        setValid(false);

        setException(e);
    }

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError (SAXParseException e) throws SAXException
    {
        setValid(false);

        setException(e);
    }

    /**
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning (SAXParseException e) throws SAXException
    {
        setValid(false);

        setException(e);
    }

    /**
     * @return boolean denoting the result of the validation
     */
    public boolean isValid ()
    {
        return this.valid;
    }

    private void setValid (boolean valid)
    {
        this.valid = valid;
    }

    /**
     * Resets the state of the class
     */
    public void reset ()
    {
        setValid(true);
    }

    private void setException (SAXParseException exception)
    {
        this.exception = exception;
    }

    /**
     * @return Any {@link SAXParseException} that may have been generated
     */
    public SAXParseException getException ()
    {
        return this.exception;
    }
}