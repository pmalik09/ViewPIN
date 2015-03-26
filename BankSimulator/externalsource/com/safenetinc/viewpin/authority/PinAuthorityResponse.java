package com.safenetinc.viewpin.authority;

public class PinAuthorityResponse {

	String viewPinResponseDocument;
	int errorCode;
	boolean isSuccess;

	public PinAuthorityResponse(){
	
		this.viewPinResponseDocument = null;
		this.errorCode = 0;
		this.isSuccess = false;
	}
	
	 /**
     * Constructor
     * 
     * @param viewPinResponseDocument The response(pin Change or pin Retrieval) to be returned
     * @param errorCode The error code set in case of error condition
     * @param isSuccess The boolean isSuccess to state whether the method was successful (true) or failed (false)
     *
     */
	public PinAuthorityResponse(String viewPinResponseDocument, int errorCode, boolean isSuccess){
		
		setViewPinResponseDocument(viewPinResponseDocument);
		setErrorCode(errorCode);
		setIsSuccess(isSuccess);
	}
	
	 /**
	 * @return The viewPinResponseDocument for the card
	 */
	public String getViewPinResponseDocument()
	{
		return this.viewPinResponseDocument;
	}
	
	public void setViewPinResponseDocument(String viewPinResponseDocument)
	{
		this.viewPinResponseDocument = viewPinResponseDocument;
	}
	
	/**
	 * @return The errorCode for the card
	 */
	public int  getErrorCode()
	{
		return this.errorCode;
	}
	
	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * @return The Success result for the card
	 */
	public boolean getIsSuccess()
	{
		return this.isSuccess;
	}
	public void setIsSuccess(boolean isSuccess)
	{
		this.isSuccess = isSuccess;
	}
	
}
