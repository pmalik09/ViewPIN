
public class PinRetrievalRequest
{
    private String pinRetrievalRequest = null;
    private String wrappedSessionKey = null;
    
    public PinRetrievalRequest(String pinRetrievalRequest, String wrappedSessionKey)
    {
        super();
        
        setPinRetrievalRequest(pinRetrievalRequest);
        setWrappedSessionKey(wrappedSessionKey);
    }

    public String getPinRetrievalRequest()
    {
        return this.pinRetrievalRequest;
    }

    private void setPinRetrievalRequest(String pinRetrievalRequest)
    {
        this.pinRetrievalRequest = pinRetrievalRequest;
    }

    public String getWrappedSessionKey()
    {
        return this.wrappedSessionKey;
    }

    private void setWrappedSessionKey(String wrappedSessionKey)
    {
        this.wrappedSessionKey = wrappedSessionKey;
    }
}
