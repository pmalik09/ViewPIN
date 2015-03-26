package com.safenetinc.viewpin.cli.importeftkeys;
import iaik.pkcs.pkcs11.wrapper.CK_ATTRIBUTE;

import java.util.ArrayList;

public class CryptoKiAttributes 
{
	private ArrayList<CryptoKiAttribute> attributes = null;
	
	public CryptoKiAttributes()
	{
		super();
		
		setAttributes(new ArrayList<CryptoKiAttribute>());
	}
	
	public void add(CryptoKiAttribute attribute)
	{
		getAttributes().add(attribute);
	}
	
	private void setAttributes(ArrayList<CryptoKiAttribute> attributes) 
	{
		this.attributes = attributes;
	}
	
	public ArrayList<CryptoKiAttribute> getAttributes() 
	{
		return attributes;
	}
	
	public CK_ATTRIBUTE[] toArray()
	{
		CK_ATTRIBUTE[] attributeArray = new CK_ATTRIBUTE[getAttributes().size()];
		
		for(int i = 0; i < attributeArray.length; i++)
		{
			CK_ATTRIBUTE nextAttribute = new CK_ATTRIBUTE();
			nextAttribute.type =  getAttributes().get(i).getType();
			nextAttribute.pValue = getAttributes().get(i).getValue();
			
			attributeArray[i] = nextAttribute;
		}
		
		return attributeArray;
	}
}
