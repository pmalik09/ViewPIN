// $Id: PinAgent/src/com/safenetinc/ds/KeyValue.java 1.1 2008/09/04 10:45:13IST Mkhurana Exp  $
package com.safenetinc.ds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Abstract class to represent a KeyValue
 * 
 * @author Stuart Horler
 *
 *
 */
public abstract class KeyValue
{
    KeyValue()
    {
        super();
    }
    
    abstract Element toDom(Document document);
}
