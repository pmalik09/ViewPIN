
public class ResponseTime
{
    private long request = 0L;
    private long response = 0L;
    
    public ResponseTime(long request, long response)
    {
        super();
        
        setRequest(request);
        setResponse(response);
    }

    public long getRequest()
    {
        return this.request;
    }

    private void setRequest(long request)
    {
        this.request = request;
    }

    public long getResponse()
    {
        return this.response;
    }

    private void setResponse(long response)
    {
        this.response = response;
    }
}