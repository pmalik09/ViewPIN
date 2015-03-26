// $Id: PinAgent/src/com/safenetinc/viewpin/common/xml/SimpleNamespaceContext.java 1.1 2008/09/04 10:47:44IST Mkhurana Exp  $
package com.safenetinc.viewpin.common.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;

/**
 * Class to manage the various namespaces when parsing XML messages
 * 
 * @author Stuart Horler
 * 
 */
public class SimpleNamespaceContext implements NamespaceContext
{
    private HashMap<String,String> namespaces = null;

    /**
     * Constructor
     */
    public SimpleNamespaceContext()
    {
        super();

        setNamespaces(new HashMap<String,String>());
    }

    /**
     * Adds a namespace to our context
     * 
     * @param prefix The prefix to store against
     * @param uri The URI to store
     */
    public void addNamespace(String prefix, String uri)
    {
        getNamespaces().put(prefix, uri);
    }

    /**
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    public String getNamespaceURI(String prefix)
    {
        return getNamespaces().get(prefix);
    }

    /**
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
     */
    public String getPrefix(String namespaceUri) 
    {
        String prefix;
        Set<String> prefixes;
        Iterator<String> prefixIterator;
        String nextPrefix;
        String nextNamespaceUri;

        prefix = null;
        prefixes = null;
        prefixIterator = null;
        nextPrefix = null;
        nextNamespaceUri = null;

        // Get prefix keys
        prefixes = getNamespaces().keySet();

        // Get iterator over prefix keys
        prefixIterator = prefixes.iterator();
        
        // Work through each prefix key
        while(prefixIterator.hasNext() == true) 
        {
            // Get next prefix key
            nextPrefix = prefixIterator.next();

            // Get namespace uri value held under next prefix key
            nextNamespaceUri = getNamespaces().get(nextPrefix);

            // Is this the namespace uri we are looking for?
            if(nextNamespaceUri.equals(namespaceUri) == true)
            {
                // This is the namespace uri we are looking for
                prefix = nextPrefix;

                break;
            }
        }
        
        return prefix;
    }

    /**
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
     */
    public Iterator<String> getPrefixes(String namespaceUri) 
    {
        ArrayList<String> prefixes;
        Set<String> prefixKeys;
        Iterator<String> prefixIterator;  
        String nextPrefix;
        String nextNamespaceUri;

        prefixes = null;
        prefixKeys = null;
        prefixIterator = null;
        nextPrefix = null;
        nextNamespaceUri = null;

        // Instantiate object to hold prefixes mapped to namespace uris
        prefixes = new ArrayList<String>();
        
        // Get prefix keys
        prefixKeys = getNamespaces().keySet();
        
        // Get iterator over prefix keys
        prefixIterator = prefixKeys.iterator();

        // Work through each prefix key
        while(prefixIterator.hasNext() == true) 
        {
            // Get next prefix key
            nextPrefix = prefixIterator.next();
            
            // Get namespace uri mapped to next prefix key
            nextNamespaceUri = getNamespaces().get(nextPrefix);

            // Is this the namespace uri we are looking for?
            if(nextNamespaceUri.equals(namespaceUri) == true)
            {
                // This is the namespace uri we are looking for
                prefixes.add(nextPrefix);
            }
        }

        return prefixes.iterator();
    }
    
    private void setNamespaces(HashMap<String,String> namespaces)
    {
        this.namespaces = namespaces;
    }

    private HashMap<String,String> getNamespaces()
    {
        return this.namespaces;
    }
}