package com.safenetinc.viewpin.banksimulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.AbstractList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Random;
import java.util.Enumeration;
import java.security.*;
import java.security.cert.CertificateException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

import org.xml.sax.SAXException;
import javax.xml.transform.*;
import org.xml.sax.InputSource;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//import org.apache.commons.codec.binary.Hex;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import java.io.StringReader;
import com.safenetinc.viewpin.banksimulator.config.BankSimulatorCustomerConfiguration;
//import com.safenetinc.viewpin.banksimulator.config.BankSimulatorUser;


import com.safenetinc.viewpin.authority.ExpiryDate;
import com.safenetinc.viewpin.authority.exceptions.InvalidExpiryDateException;

//import com.safenetinc.viewpin.authority.Pin;
//import com.safenetinc.viewpin.authority.PinAuthorityResponse;
//import com.safenetinc.viewpin.authority.PrimaryAccountNumber;
/*
import com.safenetinc.viewpin.banksimulator.PINBlock;

import com.safenetinc.viewpin.authority.CardHolderDetails;
import com.safenetinc.viewpin.authority.exceptions.CardAccountLockedException;
import com.safenetinc.viewpin.authority.exceptions.CardHolderAuthenticationException;
import com.safenetinc.viewpin.authority.exceptions.DuplicateCardAccountException;

import com.safenetinc.viewpin.authority.exceptions.InvalidPinException;
import com.safenetinc.viewpin.authority.exceptions.InvalidPrimaryAccountNumberException;

import com.safenetinc.viewpin.authority.exceptions.UnknownCardAccountException;
*/
import com.safenetinc.viewpin.banksimulator.InvalidPinBlockFormatException;
import com.safenetinc.viewpin.authority.ErrorCodesConstants;

import com.safenetinc.viewpin.authority.EncryptDecrypt;


import com.safenetinc.viewpin.banksimulator.CardPin;
import com.safenetinc.viewpin.banksimulator.PrimaryAccountNumber;
import com.safenetinc.viewpin.banksimulator.InvalidPinException;
import com.safenetinc.viewpin.banksimulator.InvalidPrimaryAccountNumberException;
import com.safenetinc.viewpin.banksimulator.Pin;
import com.safenetinc.viewpin.banksimulator.PinAuthorityResponse;
import com.safenetinc.viewpin.banksimulator.RetrievePinServiceRequest;
//import com.safenetinc.viewpin.banksimulator.CardHolderDetails;


//import org.bouncycastle.util.encoders.Hex;
import javax.crypto.SecretKey; 
import javax.crypto.SecretKeyFactory; 

/**
 * Class to emulate the role of a bank in the ViewPIN transaction. This servlet receives customer details
 * before passing them onto the Authority back end.
 *
 */
public class BankSimulatorServlet extends HttpServlet
{
    /**
     * The standard serialization UID
     */
    public static final long   serialVersionUID                 = 001;

    /**
     * The PIN parameter - used to read the value of the PIN POSTed here by the user
     */
    private static String      PARAMETER_PIN                    = "pin";
    private static final String VALID_PIN_REGEX = "^[0-9]{4,12}$";


    /**
     * The PAN parameter - used to read the value of the PAN POSTed here by the user
     */

    private static String      PARAMETER_PAN                    = "pan";

    /**
     * The expiry date parameter - used to read the value of the expiry date POSTed here by the user
     */
    private static String      PARAMETER_EXPIRY_DATE            = "expiryDate";

    /**
     * The ID parameter - used to read the value of the ID POSTed here by the user. In a real banking system
     * this would be some kind of private key identifier.
     */
    private static String      PARAMETER_ID                     = "id";

    /**
     * The password parameter - used to read the value of the desired customer password. POSTed here by the
     * user
     */
    private static String      PARAMETER_PASSWORD               = "password";

    /**
     * The firstname parameter - used to read the value of the customer's firstname. POSTed here by the user
     */
    private static String      PARAMETER_FIRSTNAME              = "firstname";

    /**
     * The surname parameter - used to read the value of the PIN POSTed here by the user
     */
    private static String      PARAMETER_SURNAME                = "surname";

    /**
     * The subject key identifier parameter - used to read the value of the SKI. POSTed here by the user. In a
     * real banking system this would already be held on database for this customer.
     */
    public static final String SUBJECT_KEY_IDENTIFIER_PARAMETER = "subjectKeyIdentifier";

    /**
     * The customer configuration parameter - this is used to reference the data held in the HTTPSession once
     * the customer has logged in to the bank simulator. In a real scenario this would be handled by the
     * authentication system for the bank.
     */
    public static final String CUSTOMER_CONFIGURATION_PARAMETER = "customersconfiguration";

    /**
     * PIN Change Request Identifier
     */
    public static final int  PIN_CHANGE_REQUEST               			   = 01;

    /**
     * PIN View Request Identifier
     */
    public static final int  PIN_VIEW_REQUEST              				   = 02;

    public static String REQUEST_TYPE										= "requesttype";

    /**
     * The BankSimulatorConfiguration object. This is used to retrieve configuration entries from the
     * banksimulatorconfiguration.xml file.
     */
    private XMLConfiguration   config                        		   = new XMLConfiguration();

    /**
     * Random
     */
    private Random             random                           = new Random();

    private static final String serviceCode = "000";

    private SecureRandom randomNumberGenerator = null;
    private static Logger logger = Logger.getLogger(BankSimulatorServlet.class);
    private static final String ENFORCE_EXPIRY_DATE_AUTHENTICATION_PARAMETER = "enforceexpirydateauthentication";

    /**
     * Servlet doGet. Models the behaviour a ViewPIN customer is required to implement within their existing
     * website infrastructure. The flow for this method is as follows:
     * The user makes a get request to this servlet as a result of a 302 redirect from the PINAgent
     * This servlet reads the PINRetrievalRequest cookie that has been set in the user's browser by the PINAgent
     * This servlet makes a call to the PIN Authority which will process the PINRetrievalRequest message
     * The PIN Authority processess the PINRetrievalRequest message and creates an associated PINRetrievalResponse message
     * This servlet sets the PINRetrievalResponse message as a cookie and redirects the user to the PIN display page
     *
     * This example code is provided as an illustration of what is required in order to implement a ViewPIN system.
     *
     * @param request The {@link HttpServletRequest}
     * @param response The {@link HttpServletResponse}
     */
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        /*
         * First see if our user is logged in. It is assumed that ViewPIN customers already have a well
         * established method of authenticating users, however this logic is placed here for demonstration
         * completeness.
         */
        BankSimulatorUser user = (BankSimulatorUser) request.getSession().getAttribute(LoginServlet.USER_PARAMETER);
        BankSimulatorUser userChangePIN = (BankSimulatorUser) request.getSession().getAttribute(ChangePINServlet.USER_PARAMETER);
		//CardHolderDetails cardHolderDetails = new CardHolderDetails();
		//CardHolderDetails changePINCardHolderDetails = new CardHolderDetails();
        //CardHolderDetails processedCardHolderDetails=null;
        
       
       
        int requestType = 0;
        int changePINID = 0;
        /*
         * If we get to this point we assume that the user is logged in. Next we proceed with getting the
         * ViewPinRequest/ViewPinChange cookie containing the request for a PIN
         */
         String viewPinRequest = null;
         
         javax.servlet.http.Cookie[] cookies = request.getCookies();
         requestType = Integer.parseInt(request.getParameter(REQUEST_TYPE));

         if(cookies == null)// If we couldn't find the cookie then display an error message to the user.
         {
             getLogger().error("no cookies found");

             sendPINAuthorityErrorMessage(response,"no cookies found");
 	        return;
         }
         

         for(int i = 0; i < cookies.length; i++)
         {
        	         	
             if ((requestType == 2) && (cookies[i].getName().equals(this.config.getString("PinRetrievalRequestEnvelopeCookieName(0)"))))
             {
                 viewPinRequest = cookies[i].getValue();

                 break;
             }
 			else  if ((requestType == 1) && (cookies[i].getName().equals(this.config.getString("PinChangeRequestEnvelopeCookieName(0)"))))
             {
                 viewPinRequest = cookies[i].getValue();

                 break;
             }
         }

         // Ensure that the cookie we found doesn't have a null value
         if(viewPinRequest == null)
         {
			 if(requestType == 1)
			 {
				getLogger().error("pin view request cookie not found");
	            sendPINAuthorityErrorMessage(response,"PIN change request cookie not found");
 		        return;
			 }
			
         }

    

        if(user == null)
        {
            getLogger().warn("user not logged in");

            response.sendRedirect(this.config.getString("LoginPage(0)"));

            return;
        }
        

        getLogger().debug("user logged in as " + user.getFirstname() + " " + user.getSurname());

		Service service = new Service();
        Call call = null;
 
        try
        {
            call = (Call)service.createCall();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        
         String endpoint = config.getString("PINAuthorityServiceEndPoint(0)");
		 String encodedValue = getEncodedPINBlock("1234567891234567", "1234");
		 System.out.println("encodedValue is: " + encodedValue);

        // adding serializer-deserializers
        QName    pinAuthorityResponseQn   = new QName( "urn:ViewPinAuthorityService", "PinAuthorityResponse" );
        QName cardPinQn = new QName( "http://authority.viewpin.safenetinc.com", "CardPin" );
        QName retrievePinServiceRequestQn = new QName( "http://authority.viewpin.safenetinc.com", "RetrievePinServiceRequest" );

        call.registerTypeMapping(PinAuthorityResponse.class, pinAuthorityResponseQn,
                new org.apache.axis.encoding.ser.BeanSerializerFactory(PinAuthorityResponse.class, pinAuthorityResponseQn),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(PinAuthorityResponse.class, pinAuthorityResponseQn));
        
        call.registerTypeMapping(CardPin.class, cardPinQn,
                new org.apache.axis.encoding.ser.BeanSerializerFactory(CardPin.class, cardPinQn),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(CardPin.class, cardPinQn));
        
        call.registerTypeMapping(RetrievePinServiceRequest.class, retrievePinServiceRequestQn,
                new org.apache.axis.encoding.ser.BeanSerializerFactory(RetrievePinServiceRequest.class, retrievePinServiceRequestQn),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(RetrievePinServiceRequest.class, retrievePinServiceRequestQn));

        call.setReturnType(pinAuthorityResponseQn);
        
        call.addParameter("PinAuthorityResponse",pinAuthorityResponseQn, PinAuthorityResponse.class, ParameterMode.OUT );
        call.addParameter("RetrievePinServiceRequest",retrievePinServiceRequestQn, RetrievePinServiceRequest.class, ParameterMode.IN );

        call.setTargetEndpointAddress(new URL(endpoint));
        call.setOperationName(new QName("retrievePin"));

        CardPin cardPinOne = new CardPin();
        cardPinOne.setPrimaryAccountNumber("1234567891234567");
       // cardPinOne.setEncodedEncryptedPinBlock("8e92aa416498ab4b");
	    cardPinOne.setEncodedEncryptedPinBlock(encodedValue);
        cardPinOne.setZonePinKeyName("ZPK");
        
        CardPin cardPinTwo = new CardPin();
        cardPinTwo.setPrimaryAccountNumber("1234567891234567");
    //    cardPinTwo.setEncodedEncryptedPinBlock("8e92aa416498ab4b");
	    cardPinTwo.setEncodedEncryptedPinBlock(encodedValue);
        cardPinTwo.setZonePinKeyName("ZPK");
        
		System.out.println(viewPinRequest);
        RetrievePinServiceRequest retrievePinServiceRequest = new RetrievePinServiceRequest();
        retrievePinServiceRequest.setCardVerificationKeyPairName("CVK");
       // retrievePinServiceRequest.setEncodedCompressedPinRetrievalRequestDocument("eNrtWFuTmk4W_yqpySNluF-cmpkqBAEBBQVReUNoBIUGueOnX0YnszGZ3Z38s49563P63H-nuw88NfmjGcMVqIoYNF6yAucalNWXLk1g-djkzw9RVeWPKNq27bfSCwEE1SiG_jc_S1FYok0M2jyGD28KQXmn0JLfsuKAEhiGodgYHWSCMj58_S7dAej_B3kcxahX-UHi68PL0xBlFadDYF6avxAYTowwekRiNk4_kuQjxrlP6J3Iq4JdeLD0_CrO4CwAsIrDGBQvGEsGdIhTIzzg8BHFUMxojJPhaEyO8WBPkkHI-FdjH6u_Gha8IlCyJACFA4qB7XuvQtetHxmOl9Tg5ek1yccp9Is-r0AgepX3xe5z8Im8hQxWg-OHexuD5Tmooiz4wieHrIirKP2ELQ-UOMGN_L3_gL48BeWjBvoZDLPr-h39N8OfjO89qcHWw5f1avb88HWqvdpHf3RwDV6I8wgUr9nfMW41YtveXlmb9WKpuX1up9uIWLpnBkjImus9VxHJKMwYe7GOls9P6C_qd6ybC_TXqr9cUf0AoIE77fK46AepGzkfCh_9Re4TyNXZiSNqn8HW_Dr1WlswLkZciAi31gOOibQ4nERQTtnjfv6nyL2BMqx2wCv-ovMJdLZhQlJOI50qBJCpm5t9ngtk6WTmtEGrPU3q6iBdJpWF_Sk6N0zQX8-SWcSpV_S872c1rBZ1ugd_wfsMeBWGVRJdWng3JXVBrSCmNiGRinZzbnXLK1ElZwjjZKWcRvF63FSmHoMA34f61umXtvCHgH6MG_pfnr97S0OmX2bB88OQ-_8DqqL0RpkH8lF6CPEP0NrS2PgW_BthabOXhk0rplq1ylk6cm6cMJOUMa0AVmj5fIXju-A78Zb_byLFjFmmOhqbuZrN1Uo5JVyIUWtn4-mcs0kuZEnlsoV2Guu0lsske9xhsErnRDDfNbLItrqqp2vEFwmkQM3-ovBqukFqpJOwZrKeJ6wH1YtAulNeimSbaXT3SApwIytjnb0YF2cXl0Yf1-VZ0VpTXbAHnBaTlUX7Yijk4vpA4bUtRkR92WIr22tS5yTLM2fTdp0nHQz5fEAsm0ntE1gQzsQrDILkXMm2S7QXGrNs9oC5GEeiCHza4DlbhKVdMuyO422Gl5cugMgcOKFF0gst23WYP1eOtWT69LYjKFo8TIUVsZMU6eJMyiRqVzspS5CxVubuTgI4tlBNcrIVOWEbqxkp61Nhs26Z3G-f_9mVNOB27QErPkCvqgvwToHgvVsED2ZwaNwkvni_1ZA49tqQI9D5Ix-n4Ne3Tnx39pud_ToNj9KsANf-LiOPoJk3kysQgmLIDNzukIcr8zqThlmRlvfk_3R4N34D2IAky0EwKr_H_eb08wY_LgX6a4xifBhm8n9cl7ua3GzdOsHNiIh29gGJRkZVigrOnUjLlrEN5LCqcPRJba5iVV04AXU76z8qXxnvFb6RP7XIO6Q3DeU0NWnQW8M3D9Z3_RqXD_Z2HegbN2MJXq0ZNA6kbocaC67Zm_xy5de0X59bRBMYhc6mVju1wAUhmibG1rY97mgsYHf0yYPKXGT7i7EXpudcOC5Ebm_DvVpZBDU-bxvFihdYnmyb8X4xTccZmctyUwthNo7V_nxo0mx9ws6lGG4Rmp6fGSfzIJES4zJSi9XwKcMUeyWxteJERbkK9S04muJpFuvsIYkJhu0kbl4jzp7vx9KloNNltj9Ki1oNEnYRzGQppspVAFzdY5Ejw0cbaXXoTBiszyYCJlvd6p21by0TZio5OlKWtTGlL2gAdY2iEBkALLygMgF5j53RCsmLMU55eFCQvTo91HS4fH5-L_0Ptf75bh_W_95YWfwdPc-COqnLF16zgi2p5aeN6TC7xbiE6KF2IzWCwkTtdT_2ZQFEuLZZzTdCwLepbOhwozdrhUAM2oPImAGoxKvOEezkvq7MUM_jCSmCoFpG9JJC-21UL1k20iAnrDBDc2UYlym7pXtkLMNpJyWRQIDFhOeV6uK1orzrbczOlthJn4w1o4lnNpWjJmEeEaqgpgewDwscqY56A7Y7rVuEAk3FMcDm1WRT0BiC0iLHRmjk99J8abusOUtNOvH2Sns8HXauVkTdbnfqjEhRM2PnGkdhQViWayGbmZkmlJFMevwImuHhU1EFKmO49FHy6JNVcGCotaRuc7UQRavJIuEYS5ImY_jcmUtut5QVPscdb6-mY-wG0PcivxZ8mO8yOExgL_ySn1x33zm3Y_UjQOjP8H34TrfE5CCIzTI8n_HEPS8EqWvRgPOIGvv8O43eX_roxz82Xv4F-4XC4A..");
		//retrievePinServiceRequest.setEncodedCompressedPinRetrievalRequestDocument("eNrtl0mTokgUgP9KhX007GQRkYoqI1CxXBAVEZdbAskiS7IL_vrBpZyyq2bG7p5j3_I9Xr7ty42XPHyeO4GM0thBOfRkFGUoSZ8K3wuS5zx8rdlpGj4DcDgcvifQRAFKG06gf9exD4IE5A46hE5Qu04wkrsJB_o7ji1AEQQBCA5UNkbiWN_erQsU6P9gTwKiebKvLL7VOi9VlqnjV4lBP-xQBEk1CLZBsgrRem5yzxS9ewF3JqcJSgyDBOqpg4ORgYLUMR0UdyDSWRqyqMFxHGo0OboaaUarwTII0gbR1EiaODv7evrJcQ_GxhB7BopVFFdqHZ6Mzp8-KlToZajzciryWQj0uAxTZPRhCp-UMkQP1N3DQVoFrt37qDxPUWpj44n3LBw7qe0_4AuihKTaDV3Ta6DzYiTPE1SOAhOfxzf6V8cP5ncrqvJVe1rJo9faN2Fy8g8-Bjgn33NCG8Wn6u8Ulx6NQ2NP44EtAjCPomNIDFwE-rs2OZvzqqf04o0wYJ0DNdSS1xfwafqd6hICfO5650z1C0CVVihCJy4rq4s4rRpv_yH3ADmcTRl3YWPFwBqIBULolXaY5PuuUx430KzPpZja-GGIQ-t3yV2hVKMtgvEfOg_Q8V2bHr3V4YLjlmPKqPNic7sQ6bpiUzwLR-2dIQ96CjH1bfy7dC5MwOe9NI8dH8Ylr-s4C1Ip8zX0B94j8MyJN-Iyd5eLoutLIDFlSTMUomktkDask6N4pnWDyF7SNK22PA33OaOayR-PNmHvNEP_TaBfcwP_cv3de6oqfRoZr7Wq9v8DVZzABoYobPiWSX5Ba8MQ3CX5q7CcjDrBzGM3c6LdNY_81IqZPbWfIMzv4-LwesbxbngTrvX_HKmcSbd-TvfG8ViSKTBRe2Kh4yKdEUcxgjPaYGKzv85QjCxmHHJYE8nCJVu6GS1VC0wxv54ra6_uquKktRmHaGa01pzvRqSzzrkAWy5Gm9J29ZBmbF5lYtfcLiaYHm8xNwHL_lGQ9A09F5r0gAn4xTY6auOVsc-1ft-OuLE44CP9IOvbLZUSv7bNq16c-7p0rACmWYxuEjJuBHowwEG1GDznCH8KMkmcIDdQoTd0shl8u9K9BfvJ1XJ6YTZ8HKPzmklsSDGtq0sZmRWEQEeXfVk7K8_vPBPHfnIv_mfAuyctCnLk4RAZjeQ972vQxx1-3QrwOce-Y1Xv3F_uy11PLr4uK8EZSLtsrDj5kmWtZGxppETQdGFNnahfnzJct5cPYCANPXd02T8fJ58Vtw5fxB-WyA3pNRzSXbYtDAksbfTtm1TIyWbd3nO7gyxQrawdU6pLFQqZbjm0AHCpF_mKTiATiPxUCusbgHgMm7G1ExLubZPyVrMstba1Uoz-ktsKgssnvmCOodze-clOCr1SWBx8YZZRflZitk4f_NlIVVPTVcn1cOYYjDwumzq3GIh7SrXdzMokdjlo9fjV662cD_n_eAZV478_yEv-Tp5iI_OypMNLkrGRczZYb1eJsrIdWuTnIBiydLPHDu3I3g-cyaHVkoSVoHXfWtpKXL-VgUIMAkLiHTCBxRa7Kr33vSI9mlHUnPRXetI3I06TE5NWqXrXK8pium5KM5929tablveAmYl63SikMXQzcosnLlrg-jwd-qK4WyyJUmkOPCbf9cpyPtUO7mqjRYvwXPR74qciqrsdB9Xt2-EXfPf89aa54P9YNPixJV-e0W_1WW_X7efGdr6OKK-UbXWFmBExGLcfP6PB_eEEvv6p7fwF_1Iedg..");	
		retrievePinServiceRequest.setEncodedCompressedPinRetrievalRequestDocument(viewPinRequest);
        retrievePinServiceRequest.setExpiryDate("01/15");
        retrievePinServiceRequest.setCardPins(new CardPin[] { cardPinOne, cardPinTwo } );
        retrievePinServiceRequest.setPrimaryAccountNumber("1234567891234567");
        retrievePinServiceRequest.setServiceCode("000");
        retrievePinServiceRequest.setEnforceExpiryDateAuthentication(false);
        retrievePinServiceRequest.setAuthenticateCardVerificationValueOnly(true);
        
        PinAuthorityResponse pinAuthorityResponse = (PinAuthorityResponse)call.invoke(new Object [] { retrievePinServiceRequest } );
        
        if(pinAuthorityResponse.getIsSuccess() == true)
        {
        	if(retrievePinServiceRequest.isAuthenticateCardVerificationValueOnly() == false)
        	{
        		System.out.println(pinAuthorityResponse.getViewPinResponseDocument());	
        	}
        }
        else
        {
        	System.out.println(pinAuthorityResponse.getErrorCode());
        }
         
		 getLogger().debug("call to PIN Authority made");
		
        if(true == pinAuthorityResponse.getIsSuccess())
        {
			/*Cookie responseCookie = new Cookie(this.config.getString("PinRetrievalResponseEnvelopeCookieName(0)"), pinAuthorityResponse.getViewPinResponseDocument());
			responseCookie.setDomain(this.config.getString("PinRetrievalResponseCookieDomain(0)"));
			responseCookie.setSecure(false);
			responseCookie.setPath("/");
			response.addCookie(responseCookie);*/

			
			//Redirect the user to the PIN display page
			//response.sendRedirect(this.config.getString("PinDisplayPage(0)"));
			sendPINAuthoritySuccessMessage(response,"CVV Matched");
			
		}
		else
		sendPINAuthorityCVVErrorMessage (response,"CVV NOt Matched");
		
		
       
    }

    /**
     * Servlet doPost - models the behaviour of adding a customer to the bank.
     * This method simply adds a customer to the session store for the BankSimulator application.
     * It is assumed that organisations implementing the ViewPIN system already have a user enrollment
     * system. Therefore this code exists purely to provide the BankSimulator with a user list, organisations
     * implementing ViewPIN can treat this section of the example code as largely irrelevant.
     *
     * @param request The {@link HttpServletRequest}
     * @param response The {@link HttpServletResponse}
     */
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        getLogger().debug("BankSimulatorServlet do post up and running");


        String primaryAccountNumberParameter = request.getParameter(PARAMETER_PAN);
        String expiryDateParameter = request.getParameter(PARAMETER_EXPIRY_DATE);
        String id = request.getParameter(PARAMETER_ID);
        String firstname = request.getParameter(PARAMETER_FIRSTNAME);
        String surname = request.getParameter(PARAMETER_SURNAME);
        String password = request.getParameter(PARAMETER_PASSWORD);
        String PIN = request.getParameter(PARAMETER_PIN);
        int inputPINBlockFormat = config.getInt("InputPINBlockFormat(0)");
        int outputPINBlockFormat = config.getInt("OutputPINBlockFormat(0)");
        String enforceExpiryDateAuthenticatonParameter = request.getParameter(ENFORCE_EXPIRY_DATE_AUTHENTICATION_PARAMETER);

        if (id == null)
        {
            sendErrorMessage(response, "Please enter the six digit identifier for the demonstration customer");
            return;
        }
        else if (firstname == null)
        {
            sendErrorMessage(response, "Please enter the forename for the demonstration customer");
            return;
        }
        else if (surname == null)
        {
            sendErrorMessage(response, "Please enter the surname for the demonstration customer");
            return;
        }
        else if (password == null)
        {
            sendErrorMessage(response, "Please enter the password for the demonstration customer");
            return;
        }

        if(enforceExpiryDateAuthenticatonParameter == null)
        {
            sendErrorMessage(response, "Please indicate if you wish expiry date authentication to be enforced");

            return;
        }

        // Remove whitespace from enforce expiry date authentication parameter
        enforceExpiryDateAuthenticatonParameter = enforceExpiryDateAuthenticatonParameter.trim();

        boolean enforceExpiryDateAuthentication = false;

        if(enforceExpiryDateAuthenticatonParameter.equalsIgnoreCase("true") == true)
        {
            enforceExpiryDateAuthentication = true;
        }
        else if(enforceExpiryDateAuthenticatonParameter.equalsIgnoreCase("false") == true)
        {
            enforceExpiryDateAuthentication = false;
        }
        else
        {
            sendErrorMessage(response, "enforce expiry date authentication must be set to true or false");

            return;
        }

        PrimaryAccountNumber primaryAccountNumber = null;

        try
        {
            primaryAccountNumber = new PrimaryAccountNumber(primaryAccountNumberParameter);
        }
        catch(InvalidPrimaryAccountNumberException ipane)
        {
            sendErrorMessage(response, "Please enter a 16 digit primary account number");

            return;
        }

        if (id.length() != 6)
        {
            sendErrorMessage(response, "Please enter the six digit identifier for the demonstration customer");
            return;
        }
        else if (firstname.length() == 0)
        {
            sendErrorMessage(response, "Please enter the forename for the demonstration customer");
            return;
        }
        else if (surname.length() == 0)
        {
            sendErrorMessage(response, "Please enter the surname for the demonstration customer");
            return;
        }
        else if (password.length() == 0)
        {
            sendErrorMessage(response, "Please enter the password for the demonstration customer");
            return;
        }

        ExpiryDate expiryDate = null;

        try
        {
            expiryDate = new ExpiryDate(expiryDateParameter);
        }
        catch(InvalidExpiryDateException iede)
        {
            sendErrorMessage(response, "Expiry date must be of the form MM/YY");

            return;
        }

        try
        {
            Integer.parseInt(id);
        }
        catch (NumberFormatException nfe)
        {
            sendErrorMessage(response, "Identifier must be six digits");
            return;
        }

        // check if user with same id exists or not
        BankSimulatorCustomerConfiguration configuration_check = (BankSimulatorCustomerConfiguration) getServletContext().getAttribute(CUSTOMER_CONFIGURATION_PARAMETER);
        if (configuration_check != null) {
            if(configuration_check.ifUserExists(id)==true) {
                sendErrorMessage(response,"User with same id already exists in System.");
                redirectOnGeneralError(response);
                return;
            }
        }

        // Now we have some settings to work with, initialise our customer...

        String cvv = null;

        try
        {
            // Calculate card holder verification value
            cvv = calculateCardHolderVerificationValue(primaryAccountNumber, expiryDate);

        }
        catch(Exception e)
        {
            getLogger().error("calculating card holder verification value " + e.getMessage());

            redirectOnGeneralError(response);

            return;
        }

      //  CardHolderDetails cardHolderDetails = null;

        // Instantiate card account
        //cardAccount = new CardAccount(primaryAccountNumber, cvv, expiryDate, enforceExpiryDateAuthentication);


        String[] pins;


        if(PIN == null)
        {
            sendErrorMessage(response,"PIN cannot be null");
            return;
        }
        
        PIN = PIN.trim();

        if(PIN.indexOf(",") != -1)
        {

            pins = PIN.split(",");
        }
        else
        {
            pins = new String[] { PIN };
            
        }


        //list of plain PINs
        ArrayList<String>  PinList = new ArrayList(Arrays.asList(pins));
        for(int i=0;i<PinList.size();i++)
        {
        	
        	if (PinList.get(i).matches(VALID_PIN_REGEX) == false)
            {
                sendErrorMessage(response,"invalid PIN ");
                return;
            }
        }

       // cardHolderDetails = new CardHolderDetails(primaryAccountNumberParameter,PinList,cvv, expiryDateParameter,inputPINBlockFormat,outputPINBlockFormat, enforceExpiryDateAuthentication);

      
        BankSimulatorUser customer = null;

    //    customer = new BankSimulatorUser(id, firstname, surname, password, primaryAccountNumber,cardHolderDetails);
	    customer = new BankSimulatorUser(id, firstname, surname, password, primaryAccountNumber);

        // Get the current config
        BankSimulatorCustomerConfiguration configuration = (BankSimulatorCustomerConfiguration) getServletContext().getAttribute(CUSTOMER_CONFIGURATION_PARAMETER);
        if (configuration == null)
            configuration = new BankSimulatorCustomerConfiguration();

        configuration.addUser(customer);

        // Set the configuration back into the context
        getServletContext().setAttribute(CUSTOMER_CONFIGURATION_PARAMETER, configuration);

        sendSuccessMessage(request, response, cvv, configuration);
    }
	
	
    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init () throws ServletException
    {

        URL configurationFile = Thread.currentThread().getContextClassLoader().getResource("/BankSimulatorConfig.xml");

        getLogger().debug("configuration file = " + configurationFile);

        try
        {
            // Open our configuration file
            this.config.load(configurationFile);
        }
        catch(ConfigurationException ce)
        {
            getLogger().fatal("loading configuration file " + ce.getMessage());

            throw new ServletException();
        }

        try
        {
            // Obtain a random number generator for the pin authority to use
            setRandomNumberGenerator(SecureRandom.getInstance("SHA1PRNG"));

            // Trigger random number generator seeding
            getRandomNumberGenerator().nextLong();
        }
        catch(Exception e)
        {
            getLogger().fatal("instantiating random number generator " + e.getMessage());

            throw new ServletException();
        }

    }

    /**
     * Method used to render an error page to the end user.
     * @param response The HTTPServletResponse to use for returning the message
     * @param errorMessage The error message to return
     */
    private void sendErrorMessage (HttpServletResponse response, String errorMessage)
    {
        try
        {
            response.setContentType("text/html");
            PrintWriter out = new PrintWriter(response.getOutputStream());
            out.println("<html>");
            out.println("<head>");
            out.println("An Error Occurred Whilst Setting Simulator Configuration");
            out.println("</head>");
            out.println("<body");
            out.println("Safenet Bank Simulator");
            out.println("<br>");
            out.println("An error occurred when setting configuration parameters:");
            out.println("<br>");
            out.println("<br>");
            out.println(errorMessage);
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        }
        catch (IOException ioe)
        {
            getLogger().error("IOException reporting error back to user", ioe);
        }
    }
	/**
     * Method used to render an error page to the end user.
     * @param response The HTTPServletResponse to use for returning the message
     * @param errorMessage The error message to return
     */
    private void sendPINAuthorityErrorMessage (HttpServletResponse response, String errorMessage)
    {
        try
        {
            response.setContentType("text/html");
			response.addCookie(deleteCookieByName(this.config.getString("PinChangeRequestEnvelopeCookieName(0)")));
            PrintWriter out = new PrintWriter(response.getOutputStream());
            out.println("<html>");
            out.println("<head>");
            out.println("An Error Occurred Whilst Fetching/Changing PIN ");
            out.println("</head>");
            out.println("<body");
            out.println("Safenet Bank Simulator");
            out.println("<br>");
            out.println("<br>");
            out.println(errorMessage);
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        }
        catch (IOException ioe)
        {
            getLogger().error("IOException reporting error back to user", ioe);
        }
    }
	 private void sendPINAuthorityCVVErrorMessage (HttpServletResponse response, String errorMessage)
    {
        try
        {
            response.setContentType("text/html");
			response.addCookie(deleteCookieByName(this.config.getString("PinChangeRequestEnvelopeCookieName(0)")));
            PrintWriter out = new PrintWriter(response.getOutputStream());
            out.println("<html>");
            out.println("<head>");
            out.println("CVV not matched ");
            out.println("</head>");
            out.println("<body");
            out.println("Safenet Bank Simulator");
            out.println("<br>");
            out.println("<br>");
          //  out.println(errorMessage);
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        }
        catch (IOException ioe)
        {
            getLogger().error("IOException reporting error back to user", ioe);
        }
    }
	private void sendPINAuthoritySuccessMessage (HttpServletResponse response, String errorMessage)
    {
        try
        {
            response.setContentType("text/html");
			response.addCookie(deleteCookieByName(this.config.getString("PinChangeRequestEnvelopeCookieName(0)")));
            PrintWriter out = new PrintWriter(response.getOutputStream());
            out.println("<html>");
            out.println("<head>");
            out.println("CVV Matched ");
            out.println("</head>");
            out.println("<body");
            out.println("Safenet Bank Simulator");
            out.println("<br>");
            out.println("<br>");
           // out.println(errorMessage);
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        }
        catch (IOException ioe)
        {
            getLogger().error("IOException reporting error back to user", ioe);
        }
    }
	
	private void f (HttpServletResponse response, String errorMessage)
    {
        try
        {
            response.setContentType("text/html");
			response.addCookie(deleteCookieByName(this.config.getString("PinChangeRequestEnvelopeCookieName(0)")));
            PrintWriter out = new PrintWriter(response.getOutputStream());
            out.println("<html>");
            out.println("<head>");
            out.println("CVV NotMatched ");
            out.println("</head>");
            out.println("<body");
            out.println("Safenet Bank Simulator");
            out.println("<br>");
            out.println("<br>");
            //out.println(errorMessage);
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
        }
        catch (IOException ioe)
        {
            getLogger().error("IOException reporting error back to user", ioe);
        }
    }

    /**
     * Method to redirect a user to a success page. This method is used when a user is successfully added to
     * the BankSimulator. The destination page displays the newly generated CVV number for the user's card.
     *
     * @param request The HttpServletRequest to use.
     * @param response The HttpServletResponse to use.
     * @param cvv The CVV calculated for the card.
     * @param configuration The BankSimulatorCustomerConfiguration object to use.
     */
    private void sendSuccessMessage (HttpServletRequest request, HttpServletResponse response, String cvv, BankSimulatorCustomerConfiguration configuration)
    {
        request.getSession().setAttribute("cvv", cvv);
        request.getSession().setAttribute(CUSTOMER_CONFIGURATION_PARAMETER, configuration);

        try
        {
            getServletConfig().getServletContext().getRequestDispatcher("/configset.jsp").forward(request, response);
        }
        catch (Exception ioe)
        {
            getLogger().error("IOException reporting success", ioe);
        }
    }

    private synchronized void setRandomNumberGenerator(SecureRandom randomNumberGenerator)
    {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    private SecureRandom getRandomNumberGenerator()
    {
        return this.randomNumberGenerator;
    }

    private static Logger getLogger()
    {
        return logger;
    }

    private void redirectOnGeneralError(HttpServletResponse response) throws IOException
    {
        response.sendRedirect(this.config.getString("PinAgentGeneralErrorRedirectionUrl(0)"));
    }

    private void redirectOnError(HttpServletResponse response, int errorCode) throws IOException
    {
        response.sendRedirect(new String(new Integer(errorCode).toString()));
    }

    private String calculateCardHolderVerificationValue(PrimaryAccountNumber primaryAccountNumber, ExpiryDate expiryDate) throws Exception
    {
	/*
        // Get a new IV for calculation of the CVV
        byte[] ivarray = new byte[8];
        this.random.nextBytes(ivarray);
        String iv = new String(Hex.encode(ivarray));

        // now calculate the cvv
        CVV cvvCalc = new CVV();
        String cvv = null;

        cvv = cvvCalc.calculateCVV(primaryAccountNumber, expiryDate, serviceCode, iv);

        getLogger().debug("card holder verification value calculated as " + cvv);

        return cvv;
		*/
		KeyStore authorityKeyStore=null;
        Key CVKA = null;
		Key CVKB = null;
		SecretKey cvka = null;
		SecretKey cvkb = null;
		byte[] keyBytes = null;
		
		
		File keyStoreFile=null;
		
		keyStoreFile=new File("D:\\bankkey.ks");
            
         //initialize key store
        authorityKeyStore = this.initKeyStore(keyStoreFile, "changeit".toCharArray(), "JCEKS");
     
		
		CVKA = authorityKeyStore.getKey("cvka", "changeit".toCharArray());
		CVKB = authorityKeyStore.getKey("cvkb", "changeit".toCharArray());
		
		keyBytes = new byte[8];
        keyBytes = CVKA.getEncoded();
        cvka = new SecretKeySpec(keyBytes, "DES");
		
		keyBytes = new byte[8];
        keyBytes = CVKB.getEncoded();
        cvkb = new SecretKeySpec(keyBytes, "DES");
		
		NibbleBuffer nb = new NibbleBuffer(16); // 128 zero bits
    	nb.append(primaryAccountNumber.getPrimaryAccountNumber());
    	
    	// Append expiry date
    	String formattedExpiryDate = expiryDate.getFormatted(); 
        String expiryDateMonth = formattedExpiryDate.substring(0, 2);
        String expiryDateYear = formattedExpiryDate.substring(3, 5);
        nb.append(expiryDateMonth);
        nb.append(expiryDateYear);
        
        // Append 
		
        nb.append(serviceCode); // Service code
        
        getLogger().debug(new String(Hex.encodeHex(nb.getBuffer())));
    	
        byte[] panExpiryDateServiceCode = nb.getBuffer();

        // Split buffer containing primary account number, expiry date service code and zero padding into two equal blocks
        byte[] blockOne = new byte[8];
        byte[] blockTwo = new byte[8];
        System.arraycopy(panExpiryDateServiceCode, 0, blockOne, 0, 8);
        System.arraycopy(panExpiryDateServiceCode, 8, blockTwo, 0, 8);

        getLogger().debug("block1 = " + new String(Hex.encodeHex(blockOne)));
        getLogger().debug("block2 = " + new String(Hex.encodeHex(blockTwo)));
        
        IntermediateCiphertextState ics = new IntermediateCiphertextState("SunJCE");
        
		//getkeyA
		
		
        // Encrypt block one with CVK A
        ics.encrypt(blockOne, cvka);
        
        // xor block 2 then encrypt with CVK A
        ics.xorThenEncrypt(blockTwo, cvka);
        
		
		//getkeyB
        // Decrypt with CVK B
        ics.decrypt(cvkb);
        
        // Encrypt with CVK A
        ics.encrypt(cvka);
        
        byte[] finalCiphertextState = ics.getCiphertextState();
        
        NibbleBuffer finalCiphertextStateNibbleBuffer = new NibbleBuffer(finalCiphertextState);
        
        NibbleBuffer lessThanTen = new NibbleBuffer(8);
        NibbleBuffer greaterThanNine = new NibbleBuffer(8);
        
        // Split numbers into two groups, under 10 and over 9
        for(int i = 0; i < finalCiphertextStateNibbleBuffer.getTotalNibbles(); i++)
        {
        	byte nextNibble = finalCiphertextStateNibbleBuffer.getNextNibble();
        	
        	if(nextNibble < 10)
        	{
        		lessThanTen.appendNibble(nextNibble);
        	}
        	else
        	{
        		greaterThanNine.appendNibble(nextNibble);
        	}
        }
        
        // Buffer to hold sorted nibbles
        NibbleBuffer numbersThenHex = new NibbleBuffer(8);
        
        getLogger().debug("lessThanTen.getTotalNibbles() = " + lessThanTen.getTotalNibbles());
        
        lessThanTen.reset();
        
        // Concatenate both groups
        for(int i = 0; i < lessThanTen.getTotalNibbles(); i++)
        {
        	byte nextNumberNibble = lessThanTen.getNextNibble();
        	
        	numbersThenHex.appendNibble(nextNumberNibble);
        }
        
        getLogger().debug("greaterThanNine.getTotalNibbles() = " + greaterThanNine.getTotalNibbles());
        
        greaterThanNine.reset();
        
        for(int i = 0; i < greaterThanNine.getTotalNibbles(); i++)
        {
        	byte nextHexNibble = greaterThanNine.getNextNibble();
        	
        	nextHexNibble -= 10;
        	
        	numbersThenHex.appendNibble(nextHexNibble);
        }
        
        getLogger().debug("total nibbles less than 10 = " + lessThanTen.getTotalNibbles());
        getLogger().debug("total nibbles greater than 9 = " + greaterThanNine.getTotalNibbles());
        
        getLogger().debug(new String(Hex.encodeHex(lessThanTen.getBuffer())));
        getLogger().debug(new String(Hex.encodeHex(greaterThanNine.getBuffer())));
        getLogger().debug(new String(Hex.encodeHex(numbersThenHex.getBuffer())));
        
        numbersThenHex.reset();
        
        byte nextCardVerificationValueDigit = 0;
        
        StringBuffer sb = new StringBuffer();
        
        // Form recalculated CVV from first three numbers of sorted numbers
        for(int i = 0; i < 3; i++)
        {
        	nextCardVerificationValueDigit = numbersThenHex.getNextNibble();
        	
        	nextCardVerificationValueDigit += 0x30;
        	
        	sb.append((char)nextCardVerificationValueDigit);
        }
        
        String recalculatedCardVerificationValue = sb.toString();
        System.out.println("recalculatedCardVerificationValue: " + recalculatedCardVerificationValue);
		
		return recalculatedCardVerificationValue;
    }

/*

    private CardHolderDetails processCardHolderDetails(CardHolderDetails cardHolderDetails) throws InvalidPinException,InvalidPrimaryAccountNumberException, Exception
    {
        CardHolderDetails processedCardHolderDetails=null;
        HierarchicalConfiguration cardHolderPANElement=null;
        HierarchicalConfiguration cardHolderPINElement=null;
        HierarchicalConfiguration cardHolderExpiryDateElement=null;
        HierarchicalConfiguration cardHolderCVVElement=null;
        String keyStoreFileName=null;
        String keyStorePassword=null;
        String keyStoreType=null;
        String encryptionKeyName=null;
        String encryptionKeyType=null;
        String encryptionTransformation=null;
        String base64EncodedEncryptedElement=null;
        KeyStore authorityKeyStore=null;
        Key encryptionKey=null;

        File keyStoreFile=null;

        try
        {
            processedCardHolderDetails=new CardHolderDetails();
           
            //get trust store name, password, type from configuration file
            keyStoreFileName=this.config.getString("TrustStoreLocation(0)");
            keyStorePassword=this.config.getString("TrustStorePassword(0)").trim();
            keyStoreType=this.config.getString("TrustStoreType(0)");

            
            keyStoreFile=new File(keyStoreFileName);
            
            //copy the input/output PIN block format and expiryDateAuthentication 
            
            processedCardHolderDetails.setInputPINBlockFormat(cardHolderDetails.getInputPINBlockFormat());
            processedCardHolderDetails.setOutputPINBlockFormat(cardHolderDetails.getOutputPINBlockFormat());
            processedCardHolderDetails.setExpiryDateAuthenticationToBeEnforced(cardHolderDetails.isExpiryDateAuthenticationToBeEnforced());
            
           
            //initialize key store
            authorityKeyStore = this.initKeyStore(keyStoreFile, keyStorePassword.toCharArray(), keyStoreType);

            //Process PAN Element based on BankSimulator configuration file
            cardHolderPANElement=this.config.configurationAt("CardHolderDataElements.PANElement(0)");
			
            
            if(cardHolderDetails.getPrimaryAccountNumber() != null)
            {
            	
	            if (cardHolderPANElement.getBoolean("Encrypted") == true)
	            {
	            	
	                encryptionKeyName=cardHolderPANElement.getString("EncryptionProperties.KeyIdentifier(0)");
	                encryptionKeyType=cardHolderPANElement.getString("EncryptionProperties.KeyType(0)");
	                encryptionTransformation=cardHolderPANElement.getString("EncryptionProperties.Transformation(0)");
	                
	                
	
	                encryptionKey=authorityKeyStore.getKey(encryptionKeyName, keyStorePassword.toCharArray());
	              
	                //Convert the DES3 format to DESede --- this has been done to support SunJCA/JCE crypto Provider
	                //SunJCA/JCE does not support DES3..it supports DESede
	                
	              
                	if(encryptionKey.getAlgorithm().compareToIgnoreCase("DES3") == 0)
                	{
						
                	  	byte[] keyBytes = new byte[24];
                		keyBytes = encryptionKey.getEncoded();
                		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
                		
                		base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getPrimaryAccountNumber().getBytes(), key, encryptionTransformation);
                	}
                	else if (encryptionKey.getAlgorithm().compareToIgnoreCase("DES2") == 0)
                	{
						
                		//convert the DES2 to DES3 
                		byte[] keyBytes = new byte[24];
                		byte[] encodedLenArray = new byte[16];
                		
                		encodedLenArray = encryptionKey.getEncoded();
                		//copy the inital 16 bytes of DES2 as it is to DES3
	            		for(int i=0;i<16;i++)
	            			keyBytes[i] = encodedLenArray[i];
	            		//copy the first 8 byte of DES2 to last 8  bytes of DES3
	            		for(int i=16,j=0; i<24; i++,j++)
	            			keyBytes[i] = encodedLenArray[j];
	            		
	            		              		
                		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
                		
                		base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getPrimaryAccountNumber().getBytes(), key, encryptionTransformation);
                	
                	}
                	else
                	{
						
                		base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getPrimaryAccountNumber().getBytes(), encryptionKey, encryptionTransformation);

					}
	                processedCardHolderDetails.setPrimaryAccountNumber(base64EncodedEncryptedElement);

	            }
	            else
	            {
	            	
	                processedCardHolderDetails.setPrimaryAccountNumber(cardHolderDetails.getPrimaryAccountNumber());
	            }
	            
            }
            
            ArrayList<String> PinList = new ArrayList<String>();

            PinList = cardHolderDetails.getPinNumbers();
            
			if (PinList != null)
			{
			
				int PINElementCount = this.config.getMaxIndex("CardHolderDataElements.PINElement");

				ArrayList<String> encryptedPinList = new ArrayList<String>();

				ArrayList<Pin> cardAccountPins = new ArrayList<Pin>();

				
				//convert Pins into it into PINBlock
				ArrayList<byte[]> PINBlockList = new ArrayList<byte[]>();

				for(int i = 0; i < PinList.size(); i++)
				{
					byte[] PINBlocknew = null;
					PINBlock PinBlock = null;

								
					//create the PINBlock object

					if((cardHolderDetails.getInputPINBlockFormat()== 0) || (cardHolderDetails.getInputPINBlockFormat()== 3))
					{
						try
						{
							getLogger().debug("getInputPINBlockFormat from card holder details is 0/3" );
							PinBlock = new PINBlock(new Pin(PinList.get(i)), new PrimaryAccountNumber(cardHolderDetails.getPrimaryAccountNumber()));
						}
						catch(InvalidPinException e)
						{
							getLogger().error("Could not generate PIN Block: invalid Pin  " + e.getMessage());
							throw e;
						}
						catch(InvalidPrimaryAccountNumberException e)
						{
							getLogger().error("Could not generate PIN Block: invalid PAN  " + e.getMessage());
							throw e;
						}
					}
					else
					{
						try
						{

							PinBlock = new PINBlock(new Pin(PinList.get(i)));
						}
						catch(InvalidPinException e)
						{
							getLogger().error("Could not generate PIN Block: invalid Pin  " + e.getMessage());
							throw e;
						}
					}

					//convert it into the PBFO
					try
					{
						PINBlocknew = PinBlock.getPINBlock(cardHolderDetails.getInputPINBlockFormat());

					}
					catch(InvalidPINBlockFormatException e)
					{
						getLogger().error("Could not generate PIN Block: invalid PINBlockFormat  " + e.getMessage());
						throw e;

					}

					PINBlockList.add(PINBlocknew);

				}
			   

				for(int i=0;i<PINBlockList.size();i++)
				{
					//Process PIN Element based on BankSimulator configuration file
			   
					cardHolderPINElement=this.config.configurationAt("CardHolderDataElements.PINElement");
					
					byte[] PINBlockFormat = PINBlockList.get(i);
			   

					
			   
						encryptionKeyName=cardHolderPINElement.getString("EncryptionProperties.KeyIdentifier(0)");
						encryptionKeyType=cardHolderPINElement.getString("EncryptionProperties.KeyType(0)");
						encryptionTransformation=cardHolderPINElement.getString("EncryptionProperties.Transformation(0)");

						encryptionKey=authorityKeyStore.getKey(encryptionKeyName, keyStorePassword.toCharArray());
						

											
						//Convert the DES3 format to DESede --- this has been done to support SunJCA/JCE crypto Provider
						//SunJCA/JCE does not support DES3..it supports DESede
						if(encryptionKey.getAlgorithm().compareToIgnoreCase("DES3") == 0)
						{
							byte[] keyBytes = new byte[24];
							keyBytes = encryptionKey.getEncoded();
							final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
							
							base64EncodedEncryptedElement=EncryptDecrypt.encrypt(PINBlockFormat, key, encryptionTransformation);
						}
						else if (encryptionKey.getAlgorithm().compareToIgnoreCase("DES2") == 0)
						{

							
							//convert the DES2 to DES3 
							byte[] keyBytes = new byte[24];
							byte[] encodedLenArray = new byte[16];
							
							encodedLenArray = encryptionKey.getEncoded();
							//copy the inital 16 bytes of DES2 as it is to DES3
							for(int k=0;k<16;k++)
								keyBytes[k] = encodedLenArray[k];
							//copy the first 8 byte of DES2 to last 8  bytes of DES3
							for(int m=16,j=0; m<24; m++,j++)
								keyBytes[m] = encodedLenArray[j];
							
							final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
							
							base64EncodedEncryptedElement=EncryptDecrypt.encrypt(PINBlockFormat, key, encryptionTransformation);
							
						}
						else
						   base64EncodedEncryptedElement=EncryptDecrypt.encrypt(PINBlockFormat, encryptionKey, encryptionTransformation);

						encryptedPinList.add(base64EncodedEncryptedElement);
			   
											
					
				   
				}

				
				// Add the encrypted PinNumbers in  <ArrayList<String> to processed Card holderDetails
				processedCardHolderDetails.setPinNumbers(encryptedPinList);
			}
			
			//	processedCardHolderDetails.setPinNumbers(PinList);
            //Process CVV Element based on BankSimulator configuration file
            cardHolderCVVElement=this.config.configurationAt("CardHolderDataElements.CVVElement(0)");

            if (cardHolderCVVElement.getBoolean("Encrypted") == true)
            {
                encryptionKeyName=cardHolderCVVElement.getString("EncryptionProperties.KeyIdentifier(0)");
                encryptionKeyType=cardHolderCVVElement.getString("EncryptionProperties.KeyType(0)");
                encryptionTransformation=cardHolderCVVElement.getString("EncryptionProperties.Transformation(0)");

                encryptionKey=authorityKeyStore.getKey(encryptionKeyName, keyStorePassword.toCharArray());
                
                //Convert the DES3 format to DESede --- this has been done to support SunJCA/JCE crypto Provider
                //SunJCA/JCE does not support DES3..it supports DESede
                if(encryptionKey.getAlgorithm().compareToIgnoreCase("DES3") == 0)
            	{
            		byte[] keyBytes = new byte[24];
            		keyBytes = encryptionKey.getEncoded();
            		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");

            		base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getCardHolderVerificationValue().getBytes(), key, encryptionTransformation);
            	}
                else if (encryptionKey.getAlgorithm().compareToIgnoreCase("DES2") == 0)
            	{
                	//convert the DES2 to DES3 
            		byte[] keyBytes = new byte[24];
            		byte[] encodedLenArray = new byte[16];
            		
            		encodedLenArray = encryptionKey.getEncoded();
            		//copy the inital 16 bytes of DES2 as it is to DES3
            		for(int i=0;i<16;i++)
            			keyBytes[i] = encodedLenArray[i];
            		//copy the first 8 byte of DES2 to last 8  bytes of DES3
            		for(int i=16,j=0; i<24; i++,j++)
            			keyBytes[i] = encodedLenArray[j];
            		
            		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            	
            		base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getCardHolderVerificationValue().getBytes(), key, encryptionTransformation);
            	
            	}
                else
                    base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getCardHolderVerificationValue().getBytes(), encryptionKey, encryptionTransformation);


                processedCardHolderDetails.setCardHolderVerificationValue(base64EncodedEncryptedElement);
            }
            else
            {

                processedCardHolderDetails.setCardHolderVerificationValue(cardHolderDetails.getCardHolderVerificationValue());
            }

            //Process CVV Element based on BankSimulator configuration file
            cardHolderExpiryDateElement=this.config.configurationAt("CardHolderDataElements.ExpiryDateElement(0)");
            if(cardHolderDetails.getExpiryDate() != null)
            {
	            if (cardHolderExpiryDateElement.getBoolean("Encrypted") == true)
	            {
	                encryptionKeyName=cardHolderExpiryDateElement.getString("EncryptionProperties.KeyIdentifier(0)");
	                encryptionKeyType=cardHolderExpiryDateElement.getString("EncryptionProperties.KeyType(0)");
	                encryptionTransformation=cardHolderExpiryDateElement.getString("EncryptionProperties.Transformation(0)");
	
	                encryptionKey=authorityKeyStore.getKey(encryptionKeyName, keyStorePassword.toCharArray());
	                
	                //Convert the DES3 format to DESede --- this has been done to support SunJCA/JCE crypto Provider
	                //SunJCA/JCE does not support DES3..it supports DESede
	                if(encryptionKey.getAlgorithm().compareToIgnoreCase("DES3") == 0)
	            	{
	                	
	            		byte[] keyBytes = new byte[24];
	            		keyBytes = encryptionKey.getEncoded();
	            		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");

	            		base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getExpiryDate().getBytes(), key, encryptionTransformation);
	            	}
	                else if (encryptionKey.getAlgorithm().compareToIgnoreCase("DES2") == 0)
	            	{
	                	
	            		//convert the DES2 to DES3 
	            		byte[] keyBytes = new byte[24];
	            		byte[] encodedLenArray = new byte[16];
	            		
	            		encodedLenArray = encryptionKey.getEncoded();
	            		
	            		//copy the inital 16 bytes of DES2 as it is to DES3
	            		for(int i=0;i<16;i++)
	            			keyBytes[i] = encodedLenArray[i];
	            		//copy the first 8 byte of DES2 to last 8  bytes of DES3
	            		for(int i=16,j=0; i<24; i++,j++)
	            			keyBytes[i] = encodedLenArray[j];
	            		
	            		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
	            
	            		base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getExpiryDate().getBytes(), key, encryptionTransformation);
	            	    
	            	}
	                else
	                base64EncodedEncryptedElement=EncryptDecrypt.encrypt(cardHolderDetails.getExpiryDate().getBytes(), encryptionKey, encryptionTransformation);
	
	
	                processedCardHolderDetails.setExpiryDate(base64EncodedEncryptedElement);
	            }
	            else
	            {
	
	                processedCardHolderDetails.setExpiryDate(cardHolderDetails.getExpiryDate());
	            }
            }
	          
            processedCardHolderDetails.setExpiryDateAuthenticationToBeEnforced(cardHolderDetails.isExpiryDateAuthenticationToBeEnforced());

            return processedCardHolderDetails;
        }
        catch (Exception e)
        {
            getLogger().error("Error while processing CardHolderDetails ");
			e.printStackTrace();
            throw e;
        }
    }
*/
    private KeyStore initKeyStore (File keyStoreFile, char[] keyStorePassword, String keyStoreType) throws KeyStoreException, NoSuchAlgorithmException,NoSuchProviderException, CertificateException, IOException
    {
        KeyStore ks;
        FileInputStream fis;

        ks = null;
        fis = null;

        try
        {
            fis = new FileInputStream(keyStoreFile);
            ks = KeyStore.getInstance(keyStoreType,"SunJCE");
            ks.load(fis,keyStorePassword );
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }

        return ks;

        /*
         // only if  LunaKeystore is to be used
         try{
         ks = KeyStore.getInstance("Luna");
         ks.load(null, null);
         }
         catch(Throwable t)
         {
             t.printStackTrace();
         }

         return ks;
        */

    }
  /**
     * Method to obtain a cookie which can be used to delete another cookie
     * @param name The name of the cookie
     * @param domain The domain for the cookie
     * @param path The path of the cookie
     * @return The cookie to use in overwriting the original
     */
    public static Cookie deleteCookieByName (String name)
    {
        if(name == null)
        {
            throw new IllegalArgumentException ("null value passed to deleteCookieByName()");
        }
        
        Cookie cookie = new Cookie (name, "");
        
        cookie.setDomain (".mybank.com");
        cookie.setSecure (true);
        cookie.setMaxAge (0);
		cookie.setPath ("/");
        
        return cookie;
    }


	private static String getPINFromPINChangeResponse(String PINChangeResponse, String PINType)
	{
		  
				String oldEncryptedPIN;
				String newEncryptedPIN;
				DocumentBuilderFactory factory;
				DocumentBuilder builder;
				Document PINChangeResponseDocument;
				Node root;

				oldEncryptedPIN = null;
				newEncryptedPIN	= null;
				Pin pin2 = null;
				factory = null;
				builder = null;
				PINChangeResponseDocument = null;
				root = null;
							

				//System.out.println("PINChangeResponse is: " + PINChangeResponse);

				

				try
				{
					factory = DocumentBuilderFactory.newInstance();

					builder = factory.newDocumentBuilder();

					PINChangeResponseDocument = builder.parse(new InputSource(new StringReader(PINChangeResponse)));
				}
				catch (Exception e)
				{
					getLogger().error("Could not get PIN " + e.getMessage());

                    return null;
				}


				// get root node of xml tree structure
				root = PINChangeResponseDocument.getDocumentElement();
				NodeList PINTypeNodeLst = PINChangeResponseDocument.getElementsByTagName(PINType);
			    Node PINTypeNode = PINTypeNodeLst.item(0);
				return  PINTypeNode.getFirstChild().getNodeValue();


	}
	private String getEncodedPINBlock(String PAN, String Pin)
	{
			
			int format = 2;
			byte[] pb = null;
			KeyStore authorityKeyStore=null;
			 Key encryptionKey=null;
			File keyStoreFile = new File("D:\\bankkey.ks");
			try
			{
				PrimaryAccountNumber pan = new PrimaryAccountNumber(PAN);
				//Pin pin = new Pin(Pin);
				IsoPin iPin = new IsoPin(Pin);
				//get PINBlockArray
				/*PINBlock pinblock = new PINBlock(pin, pan);
				pb = pinblock.getPINBlock(format);*/
				
				
				authorityKeyStore = this.initKeyStore(keyStoreFile, "changeit".toCharArray(), "JCEKS");
			  
			  //convert the DES2 to DES3 
			/*	byte[] keyBytes = new byte[24];
				byte[] encodedLenArray = new byte[16];
			
				 encryptionKey=authorityKeyStore.getKey("ZPK", "changeit".toCharArray());
				 encodedLenArray = encryptionKey.getEncoded();
				//copy the inital 16 bytes of DES2 as it is to DES3
				for(int i=0;i<16;i++)
					keyBytes[i] = encodedLenArray[i];
				//copy the first 8 byte of DES2 to last 8  bytes of DES3
				for(int i=16,j=0; i<24; i++,j++)
					keyBytes[i] = encodedLenArray[j];
								
				   final SecretKey ZPK = new SecretKeySpec(keyBytes, "DESede");     		              		
								*/
				 // String encryptedPinBlock = EncryptDecrypt.encrypt(pb, ZPK,"DESede/ECB/NoPadding");*/
				  
				  IsoPinBlockFactory ipbf = new IsoPinBlockFactory(authorityKeyStore,"SunJCE");
				  pb = ipbf.generateIsoFormatTwoPinBlock( iPin,"ZPK","changeit");
				  
				 
				
				 String encryptedPinBlock = new String(Hex.encodeHex(pb));
			/*	 byte[] pinBlock = Hex.decodeHex(encryptedPinBlock.toCharArray());
				  try
				  {
					IsoPin ipin = ipbf.parseIsoPinBlock(pinBlock,"ZPK","changeit");
					System.out.println("done");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}*/
				 return encryptedPinBlock;
			}
			catch(InvalidPinException e)
			{
				System.out.println("invalid PIN");
					return null;
			}
			catch(InvalidPrimaryAccountNumberException e)
			{
				System.out.println("invalid InvalidPrimaryAccountNumberException");
					return null;
				
			}
			catch(Exception e)
			{	
				e.printStackTrace();
				return null;
			}
			
		  
	}
}

