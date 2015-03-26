// $Id: PinAgent/src/com/safenetinc/ds/Transforms.java 1.1 2008/09/04 10:45:31IST Mkhurana Exp  $
package com.safenetinc.ds;

import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to represent the transforms element of an XML Digital Signature
 * @author Stuart Horler
 *
 *
 */
public class Transforms
{
    private ArrayList<Transform> transforms = null;
    
    /**
     * Default constructor
     */
    public Transforms()
    {
        super();
        
        setTransforms(new ArrayList<Transform>());
    }
    
    /**
     * Adds a transform to this transforms element
     * @param transform The {@link Transform} to add
     */
    public void addTransform(Transform transform)
    {
        getTransforms().add(transform);
    }
    
    private void setTransforms(ArrayList<Transform> transforms)
    {
        this.transforms = transforms;
    }
    
    private ArrayList<Transform> getTransforms()
    {
        return this.transforms;
    }
    
    Element toDom(Document document)
    {
        Element transformsElement;
        Iterator<Transform> transformsIterator;
        Transform nextTransform;
        Element nextTransformElement;
        
        transformsElement = null;
        transformsIterator = null;
        nextTransform = null;
        nextTransformElement = null;
        
        transformsElement = document.createElementNS(SignatureConstants.DS_NS, "ds:Transforms");
        
        transformsIterator = getTransforms().iterator();
        
        while(transformsIterator.hasNext() == true)
        {
            nextTransform = transformsIterator.next();
            nextTransformElement = nextTransform.toDom(document);
            transformsElement.appendChild(nextTransformElement);
        }
        
        return transformsElement;
    }
}
