import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderGroup;
import org.apache.commons.httpclient.HttpParser;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.cookie.MalformedCookieException;

import com.safenetinc.viewpin.simulator.authority.PinAuthoritySimulator;

public class PinRetrievalRequestGenerator implements Runnable
{
    private int totalRequests = 0;
    private int totalErrors = 0;
    private PinAuthoritySimulator pinAuthoritySimulator = null;
    private String realHostname = null;
    private String virtualHostname = null;
    private int port = 0;
    
    public PinRetrievalRequestGenerator(int totalRequests, PinAuthoritySimulator pinAuthoritySimulator,
        String realHostname, String virtualHostname, int port)
    {
        super();
     
        setTotalRequests(totalRequests);
        setTotalErrors(0);
        setPinAuthoritySimulator(pinAuthoritySimulator);
        setRealHostname(realHostname);
        setVirtualHostname(virtualHostname);
        setPort(port);
    }
    
    public void run()
    {
        sendRequests();
    }
    
    private void sendRequests()
    {
        for(int i = 0; i < getTotalRequests(); i++)
        {
            try
            {
                requestPin();
            } 
            catch(Exception e)
            {
                recordError();
                e.printStackTrace();
            }
        }
    }
        
    private void requestPin() throws Exception
    {
        SecureRandom sr;
        SSLContext sc;
        PinRetrievalRequest pinRetrievalRequest;
        String pinRetrievalResponse;
        boolean rc;
        
        sr = null;
        sc = null;
        pinRetrievalResponse = null;
        rc = false;
     
        // Instantiate random number generator
        sr = SecureRandom.getInstance("LunaRNG");
        
        // Initialise SSL context
        sc = initialiseSslContext(sr);
        
        // Execute pin retrieval request
        pinRetrievalRequest = sendRequest(sc);
        
        // Did we execute pin retrieval request OK?
        if(pinRetrievalRequest == null)
        {
            // Failed to execute pin retrieval request
            System.err.println("no pin retrieval request");
            
            recordError();
            
            return;
        }
     
        // Process pin retrieval request
        pinRetrievalResponse = processRequest(pinRetrievalRequest);
        
        // Did we process pin retrieval request OK?
        if(pinRetrievalResponse ==  null)
        {
            // Failed to process pin retrieval request
            System.err.println("failed to process pin retrieval request");
        
            recordError();
            
            return;
        }
        
        // Process pin retrieval response
        rc = processResponse(pinRetrievalRequest, pinRetrievalResponse, sc);
        
        // Did we process pin retrieval response OK?
        if(rc == false)
        {
            // Failed to process pin retrieval response
            System.err.println("failed to process response");
    
            recordError();
            
            return;
        }
    }
    
    public PinRetrievalRequest sendRequest(SSLContext sc) throws Exception
    {
        PinRetrievalRequest pinRetrievalRequest;
        StringBuffer entityBody;
        Socket s;
        PrintStream out;
        InputStream in;
        String path;
        String nextLine;
        StatusLine statusLine;
        Header[] headers;
        HeaderGroup hg;
        CookieSpec cs;
        Header[] setCookieHeaders;
        Cookies cookies;
        Cookie wrappedSessionKeyCookie;
        Cookie pinRetrievalRequestCookie;
        
        pinRetrievalRequest = null;
        entityBody = null;
        s = null;
        out = null;
        in = null;
        path = null;
        nextLine = null;
        statusLine = null;
        headers = null;
        hg = null;
        cs = null;
        setCookieHeaders = null;
        cookies = null;
        wrappedSessionKeyCookie = null;
        pinRetrievalRequestCookie = null;
        
        entityBody = new StringBuffer();
        entityBody.append("cardholderverificationvalue=123&expirydatemonth=11&expirydateyear=7");/*&primaryaccountnumber=1234567812345678");*/
        
        try
        {
            s = sc.getSocketFactory().createSocket(getRealHostname(), getPort());
            s.setSoTimeout(5000);
            
            out = new PrintStream(s.getOutputStream());
            in = s.getInputStream();
            
            path = "/PinAgent/Processor/RequestPin";
            
            out.print("POST " + path + " HTTP/1.0\r\n");
            
            out.print("Host: " + getVirtualHostname() + "\r\n");
            
            out.print("Content-Length: " + entityBody.length() + "\r\n");

            out.print("Content-Type: application/x-www-form-urlencoded\r\n");
            out.print("\r\n");
            
            out.print(entityBody.toString());
            out.flush();
            
            // Get status line
            nextLine = HttpParser.readLine(in, "ASCII");
            
            // Parse status line
            statusLine = new StatusLine(nextLine);
            
            // Did we get redirected as exepected?
            if(statusLine.getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY)
            {
                // We did not get redirected as expected
                System.err.println("302 expected");
                
                return null;
            }
            
            headers = HttpParser.parseHeaders(in, "ASCII");
            hg = new HeaderGroup();
            
            for(int i = 0; i < headers.length; i++)
            {
                hg.addHeader(headers[i]);
            }
      
            cs = CookiePolicy.getCookieSpec(CookiePolicy.NETSCAPE);
            
            setCookieHeaders = hg.getHeaders("Set-Cookie");
            
            // Did we get Set-Cookie headers?
            if(setCookieHeaders == null)
            {
                // Failed to get Set-Cookie headers
                System.err.println("no Set-Cookie headers found");
                
                return null;
            }
            
            cookies = buildCookies(setCookieHeaders, cs, getRealHostname(), getPort(), path, true);
            
            // Get wrapped session key cookie
            wrappedSessionKeyCookie = cookies.getFirst("wrappedsessionkey");
            
            // Get pin retrieval request cookie
            pinRetrievalRequestCookie = cookies.getFirst("pinretrievalrequest");
            
            // Did we get wrapped session key cookie and pin retrieval request cookie OK?
            if(wrappedSessionKeyCookie == null || pinRetrievalRequestCookie == null)
            {
                // Failed to get wrapped session key cookie and pin retrieval request cookie
                System.err.println("pin retrieval cookies not found");
            
                return null;
            }
            
            // Instantiate pin retrieval request cookie
            pinRetrievalRequest = new PinRetrievalRequest(pinRetrievalRequestCookie.getValue(),
                wrappedSessionKeyCookie.getValue());
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }

            if(out != null)
            {
                out.close();
            }

            if(s != null)
            {
                s.close();
            }
        }
        
        return pinRetrievalRequest;
    }
    
    private String processRequest(PinRetrievalRequest pinRetrievalRequest)
    {
        String pinRetrievalResponse;
        
        pinRetrievalResponse = null;
        
        try
        {
            // Process pin retrieval request
            pinRetrievalResponse = getPinAuthoritySimulator().process(pinRetrievalRequest.getPinRetrievalRequest(),
                "123", "1234567812345678", "1234", "1", "99");
            
            // Did we process pin retrieval request OK?
            if(pinRetrievalResponse == null)
            {
                // Failed to process pin retrieval request
                System.out.println("no pin retrieval response");
                
                return null;
            }
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return pinRetrievalResponse;
    }
    
    private boolean processResponse(PinRetrievalRequest pinRetrievalRequest, String pinRetrievalResponse, SSLContext sc) throws Exception
    {
        boolean rc;
        Socket s;
        PrintStream out;
        InputStream in;
        String path;
        String nextLine;
        StatusLine statusLine;

        rc = false;
        s = null;
        out = null;
        in = null;
        path = null;
        nextLine = null;
        statusLine = null;
        
        try
        {
            s = sc.getSocketFactory().createSocket(getRealHostname(), getPort());
            s.setSoTimeout(5000);
            
            out = new PrintStream(s.getOutputStream());
            in = s.getInputStream();
            
            path = "/PinAgent/Processor/ReceivePin.js";
            
            out.print("GET " + path + " HTTP/1.0\r\n");
            
            out.print("Host: " + getVirtualHostname() + "\r\n");
            
            out.print("Cookie: wrappedsessionkey=" + pinRetrievalRequest.getWrappedSessionKey() + "\r\n");
            out.print("Cookie: pinretrievalresponse=" + pinRetrievalResponse + "\r\n");
            out.print("\r\n");
            out.flush();
            
            nextLine = HttpParser.readLine(in, "ASCII");
            
            statusLine = new StatusLine(nextLine);
            
            if(statusLine.getStatusCode() != HttpStatus.SC_OK)
            {
                System.err.println(HttpStatus.SC_OK + " expected, got " + statusLine.getStatusCode());
                
                return false;
            }
            
            BufferedReader br = null;
            
            try
            {
                br = new BufferedReader(new InputStreamReader(in));
                
                do
                {
                    nextLine = br.readLine();
                }while(nextLine != null);
                
            }
            finally
            {
                if(br != null)
                {
                    br.close();    
                }
            }
             
            rc = true;
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }

            if(out != null)
            {
                out.close();
            }

            if(s != null)
            {
                s.close();
            }
        }
        
        return rc;
    }

    private Cookies buildCookies(Header[] setCookieHeaders, CookieSpec cookieSpec, String host, int port, String path, boolean secure) throws MalformedCookieException, IllegalArgumentException
    {
        Cookies cookies;
        Header nextSetCookieHeader;
        Cookie[] nextCookies;
        
        cookies = null;
        nextSetCookieHeader = null;
        nextCookies = null;
        
        cookies = new Cookies();
        
        for(int i = 0; i < setCookieHeaders.length; i++)
        {
            nextSetCookieHeader = setCookieHeaders[i];
            
            nextCookies = cookieSpec.parse(host, port, path, true, nextSetCookieHeader);
            
            for(int j = 0; j < nextCookies.length; j++)
            {
                cookies.add(nextCookies[j]);
            }
        }
        
        return cookies;
    }

    public int getTotalErrors()
    {
        return this.totalErrors;
    }

    private void setTotalErrors(int totalErrors)
    {
        this.totalErrors = totalErrors;
    }

    public int getTotalRequests()
    {
        return this.totalRequests;
    }

    private void setTotalRequests(int totalRequests)
    {
        this.totalRequests = totalRequests;
    }

    private PinAuthoritySimulator getPinAuthoritySimulator()
    {
        return this.pinAuthoritySimulator;
    }

    private void setPinAuthoritySimulator(PinAuthoritySimulator pinAuthoritySimulator)
    {
        this.pinAuthoritySimulator = pinAuthoritySimulator;
    }
    
    private void recordError()
    {
        setTotalErrors(getTotalErrors() + 1);
    }
    
    private SSLContext initialiseSslContext(SecureRandom sr) throws NoSuchAlgorithmException, KeyManagementException
    {
        SSLContext sc;
        TrustAllCertificates tac;
        
        sc = null;
        tac = null;
        
        // Initialise SSL context
        sc = SSLContext.getInstance("SSL");
        tac = new TrustAllCertificates();
        sc.init(null, new TrustManager[] { tac }, sr); 
    
        return sc;
    }

    public String getRealHostname()
    {
        return this.realHostname;
    }

    private void setRealHostname(String realHostname)
    {
        this.realHostname = realHostname;
    }

    public String getVirtualHostname()
    {
        return this.virtualHostname;
    }

    private void setVirtualHostname(String virtualHostname)
    {
        this.virtualHostname = virtualHostname;
    }

    public int getPort()
    {
        return this.port;
    }

    private void setPort(int port)
    {
        this.port = port;
    }
}