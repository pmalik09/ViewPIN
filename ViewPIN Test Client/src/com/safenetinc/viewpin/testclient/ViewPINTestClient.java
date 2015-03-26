/*
 * Created on 3 Oct 2007
 * 
 * 
 */
package com.safenetinc.viewpin.testclient;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

/**
 * Exercises the ViewPIN PIN Agent by making requests and responses.
 * 
 * @author Paul Hampton
 */
public class ViewPINTestClient extends Thread
{
    private static final String PIN_RETRIEVAL_RESPONSE_JAVASCRIPT_URL   = "https://vp.viewmypin.co.uk/PinAgent/Processor/ReceivePin.js";

    private static final String PIN_AUTHORITY_SIMULATOR_URL             = "https4443://www2.viewmypin.co.uk:4443/banksimulator/bank";

    private static final String PASSWORD_VALUE                          = "password";

    private static final String PASSWORD_PARAMETER                      = "password";

    private static final String ID_VALUE                                = "123456";

    private static final String ID_PARAMETER                            = "id";

    private static final String CARDHOLDER_VERIFICATION_VALUE_PARAMETER = "cardholderverificationvalue";

    private static final String PIN_RETRIEVAL_REQUEST_POST_URL          = "https://vp.viewmypin.co.uk/PinAgent/Processor/RequestPin";

    private static final String BANK_SIMULATOR_LOGIN_URL                = "https4443://www2.viewmypin.co.uk:4443/banksimulator/login";

    private static final String CVV_VALUE                               = "123";

    private int                 numberOfTests;

    HttpClient                  client                                  = new HttpClient();

    Protocol                    easyHttps                               = null;

    Protocol                    easyHttps4443                           = null;

    Cookie                      wrappedSessionKey                       = null;

    Cookie                      pinRetrievalResponseCookie              = null;

    /**
     * Constructor for {@link ViewPINTestClient}. Sets the commons {@link HttpClient} parameters.
     */
    public ViewPINTestClient()
    {
        super();

        easyHttps = new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443);
        easyHttps4443 = new Protocol("https4443", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 4443);
        Protocol.registerProtocol("https", easyHttps);
        Protocol.registerProtocol("https4443", easyHttps4443);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
        client.getHttpConnectionManager().getParams().setSoTimeout(2000);
    }

    /**
     * Configures the thread's parameters, obtains the wrapped session key and corresponding
     * PinRetrievalResponse
     */
    public void setup ()
    {
        // First we need to log our 'customer' into the bank simulator
        loginToBankSimulator();
        // Now make first call to get the pin retrieval request we will be reusing
        System.out.println("Caching PIN retrieval response");
        testPINRetrievalRequest();
        Cookie[] cookies = client.getState().getCookies();

        for (int i = 0; i < cookies.length; i++)
        {
            if (cookies[i].getName().equals("wrappedsessionkey"))
            {
                wrappedSessionKey = cookies[i];
            }
        }

        // Now call the authority to get a PIN Retrieval Response message to work with
        callPinAuthority();

        cookies = client.getState().getCookies();

        for (int i = 0; i < cookies.length; i++)
        {
            if (cookies[i].getName().equals("pinretrievalresponse"))
            {
                pinRetrievalResponseCookie = cookies[i];
            }
        }
        if (pinRetrievalResponseCookie == null)
        {
            System.out.println("Error - Unable to cache PIN retrieval response cookie");
            return;
        }
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run ()
    {
        runLoopingTest();
    }

    /**
     * Makes a number of PinRetrievalRequest and PinRetrievalResponse calls
     */
    public void runLoopingTest ()
    {
        ArrayList<ViewPINTestResult> results = new ArrayList<ViewPINTestResult>();
        // Now we can request some pins
        System.out.println("Starting test");
        long startTime;
        long endTime;
        for (int i = 0; i < numberOfTests; i++)
        {
            ViewPINTestResult result = new ViewPINTestResult();
            // Get the PIN Retrieval Request
            startTime = System.currentTimeMillis();
            testPINRetrievalRequest();
            endTime = System.currentTimeMillis();
            result.setPinRetrievalRequestTime(endTime - startTime);

            /*
             * Now replace the pinRetrievalRequest and wrapped session key cookies with those we stored
             * earlier. This saves the need to call the authority simulator allowing for more load to be
             * directed at the PIN Agent
             */
            client.getState().addCookie(wrappedSessionKey);
            client.getState().addCookie(pinRetrievalResponseCookie);

            // Get the PIN Retrieval Response
            startTime = System.currentTimeMillis();
            testPinRetrievalResponse();
            endTime = System.currentTimeMillis();
            result.setPinRetrievalResponseTime(endTime - startTime);
            results.add(result);
            // print the result
            System.out.println(result);
        }
        System.out.println("Thread complete, average results:");
        System.out.println("Average request time is " + calculateAverageRequestTime(results));
        System.out.println("Average response time is " + calculateAverageResponseTime(results));

        System.out.println("Test complete");

    }

    private long calculateAverageRequestTime (ArrayList<ViewPINTestResult> results)
    {
        long total = 0;
        for (int i = 0; i < results.size(); i++)
        {
            total += results.get(i).getPinRetrievalRequestTime();
        }
        return (total / results.size());
    }

    private long calculateAverageResponseTime (ArrayList<ViewPINTestResult> results)
    {
        long total = 0;
        for (int i = 0; i < results.size(); i++)
        {
            total += results.get(i).getPinRetrievalResponseTime();
        }
        return (total / results.size());
    }

    private void loginToBankSimulator ()
    {
        System.out.println("Logging into Bank Simulator");

        PostMethod method = new PostMethod(BANK_SIMULATOR_LOGIN_URL);
        method.addParameter(new NameValuePair(ID_PARAMETER, ID_VALUE));
        method.addParameter(new NameValuePair(PASSWORD_PARAMETER, PASSWORD_VALUE));

        try
        {
            client.executeMethod(method);

        }
        catch (HttpException e)
        {

            e.printStackTrace();
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
        finally
        {
            method.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }

    }

    private void callPinAuthority ()
    {
        GetMethod method = new GetMethod(PIN_AUTHORITY_SIMULATOR_URL);

        try
        {
            client.executeMethod(method);
        }
        catch (HttpException e)
        {

            e.printStackTrace();
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
        finally
        {
            method.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
    }

    private void testPinRetrievalResponse ()
    {
        GetMethod method = new GetMethod(PIN_RETRIEVAL_RESPONSE_JAVASCRIPT_URL);

        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try
        {
            client.executeMethod(method);
            // Retrieve the javascript page containing the pin
            // method.getResponseBodyAsString();
        }
        catch (ProtocolException e)
        {
            System.out.println("Socket timeout exception - server very busy");
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("Socket timeout exception - server very busy");
        }
        catch (NoHttpResponseException e)
        {
            System.out.println("Server too busy to handle requests");
        }
        catch (HttpException e)
        {

            e.printStackTrace();
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
        finally
        {
            method.releaseConnection();
            client.getHttpConnectionManager().closeIdleConnections(0);
        }
    }

    private void testPINRetrievalRequest ()
    {

        PostMethod method = new PostMethod(PIN_RETRIEVAL_REQUEST_POST_URL);
        method.addParameter(new NameValuePair(CARDHOLDER_VERIFICATION_VALUE_PARAMETER, CVV_VALUE));

        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
        try
        {
            client.executeMethod(method);
        }
        catch (ProtocolException e)
        {
            System.out.println("Socket timeout exception - server very busy");
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("Socket timeout exception - server very busy");
        }
        catch (NoHttpResponseException e)
        {
            System.out.println("Server too busy to handle requests");
        }
        catch (HttpException e)
        {

            e.printStackTrace();
        }
        catch (IOException e)
        {

            e.printStackTrace();
        }
        finally
        {
            method.releaseConnection();
        }

    }

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main (String[] args) throws InterruptedException
    {
        int numberOfThreads = Integer.parseInt(args[0]);
        int numberOfTestsPerThread = Integer.parseInt(args[1]);
        ArrayList<ViewPINTestClient> clients = new ArrayList<ViewPINTestClient>();

        for (int i = 0; i < numberOfThreads; i++)
        {
            ViewPINTestClient client = new ViewPINTestClient();
            client.setNumberOfTests(numberOfTestsPerThread);
            client.setup();
            clients.add(client);

            System.out.println("Client configured and ready");
        }

        System.out.println("All clients configured, starting load test");

        for (int i = 0; i < numberOfThreads; i++)
        {
            clients.get(i).start();
        }

        long start = System.currentTimeMillis();

        while (true)
        {
            boolean allFinished = true;
            for (int i = 0; i < numberOfThreads; i++)
            {

                if (clients.get(i).isAlive())
                    allFinished = false;
            }
            if (allFinished)
                break;
            sleep(1000);
        }

        long end = System.currentTimeMillis();
        System.out.println("All done in " + (end - start) / 1000 + " secs");

        System.exit(0);
    }

    /**
     * @param numberOfTests the numberOfTests to set
     */
    public void setNumberOfTests (int numberOfTests)
    {
        this.numberOfTests = numberOfTests;
    }

}
