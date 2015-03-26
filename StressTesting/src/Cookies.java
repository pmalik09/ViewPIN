import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.httpclient.Cookie;

public class Cookies
{
    private ArrayList<Cookie> cookies = null;
    
    public Cookies()
    {
        super();
        
        setCookies(new ArrayList<Cookie>());
    }
    
    public void add(Cookie cookie)
    {
        getCookies().add(cookie);
    }
    
    public Iterator<Cookie> getAll()
    {
        return getCookies().iterator();
    }
    
    public Cookie getFirst(String name)
    {
        Cookie cookie;
        Cookie nextCookie;
        
        cookie = null;
        nextCookie = null;
        
        for(int i = 0; i < getCookies().size(); i++)
        {
            nextCookie = getCookies().get(i);
            
            if(nextCookie.getName().compareTo(name) == 0)
            {
                cookie = nextCookie;
                
                break;
            }
        }
        
        return cookie;
    }

    private ArrayList<Cookie> getCookies()
    {
        return this.cookies;
    }

    private void setCookies(ArrayList<Cookie> cookies)
    {
        this.cookies = cookies;
    }
}
