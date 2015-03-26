<%--$Id: PinAgent/src/jsp/showPin.jsp 1.2 2012/07/19 11:20:57IST Malik, Pratibha (Pmalik) Exp  $--%>
<%@page import="com.safenetinc.viewpin.agent.otp.PadEncryptedPin"%>
<%@page import="com.safenetinc.viewpin.agent.otp.PadEncryptedPins"%>
<%response.addHeader("P3P","CP=\"IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT\"");
response.addHeader("Pragma", "no-cache"); 
response.addHeader("Cache-Control", "no-cache"); 

%>

function decryptPIN(index)
{
    var encryptedPins = new Array();
    var padKeyCookieNames = new Array();
    
    <%
	    PadEncryptedPins padEncryptedPins = (PadEncryptedPins)request.getAttribute("com.safenetinc.viewpin.agent.otp.PadEncryptedPins");
        
        // Session no longer required, clean up to avoid resource leakage
        request.getSession().invalidate();
        
        PadEncryptedPin nextPadEncryptedPin = null;
	
        for(int i = 0; i < padEncryptedPins.getTotalPinNumbers(); i++)
        {
        	nextPadEncryptedPin = padEncryptedPins.getPadEncryptedPin(i);
    %>
        	encryptedPins[<%=i%>] = "<%=nextPadEncryptedPin.getPadEncryptedPin()%>";
        	padKeyCookieNames[<%=i%>] = "<%=nextPadEncryptedPin.getPadKeyCookieName()%>";
    <%
        }
    %>
    
    <%-- Check that the value of index is within bounds for the number of pins we have --%>
	if(index > encryptedPins.length)
	{
		return "Index value too high";
	}
	
    index = index - 1;
    
    <%-- Get encrypted pin at requested index --%>
	var encryptedPin = encryptedPins[index];
	
	<%-- Get one time pad key cookie name at requested index --%>
	var padKeyCookieName = padKeyCookieNames[index];
	
	<%-- Extract one time pad key from cookie value --%>
    var padKey = readCookie(padKeyCookieName);
    
    <%-- Did we get one time pad key from cookie value OK? --%>
    if(padKey == -1)
    {
    	return "Please enable Javascript cookie handling on your browser and try again";
    }
    
    <%-- Delete cookie containing one time pad key --%>
    eraseCookie(padKeyCookieName);
    
    <%-- decrypt pin --%>
    var decryptedPin = decryptPin(encryptedPin, padKey);
    
    return decryptedPin;
}

function decryptPin(encryptedPin, padKey)
{
    var decryptedPin;
    var nextEncryptedPinDigit;
    var nextPadKeyDigit;
    var nextDecryptedPinDigit;
    
    decryptedPin = "";
    
    for(i = 0; i < encryptedPin.length; i++)
    {
        nextEncryptedPinDigit = encryptedPin.charAt(i);
        nextEncryptedPinDigit = fromHexCharacter(nextEncryptedPinDigit);
        
        nextPadKeyDigit = padKey.charAt(i);
        nextPadKeyDigit = fromHexCharacter(nextPadKeyDigit);
        
        nextDecryptedPinDigit = nextEncryptedPinDigit ^ nextPadKeyDigit;
        
        decryptedPin = decryptedPin + nextDecryptedPinDigit.toString();
    }
 
    return decryptedPin;
}

function decryptNextPINDigit(index)
{
    var encryptedPins = new Array();
    var padKeyCookieNames = new Array();
    
    <%
        for(int i = 0; i < padEncryptedPins.getTotalPinNumbers(); i++)
        {
        	nextPadEncryptedPin = padEncryptedPins.getPadEncryptedPin(i);
    %>
        	encryptedPins[<%=i%>] = "<%=nextPadEncryptedPin.getPadEncryptedPin()%>";
        	padKeyCookieNames[<%=i%>] = "<%=nextPadEncryptedPin.getPadKeyCookieName()%>";
    <%
        }
    %>
    
    <%-- Check that the value of index is within bounds for the number of pins we have --%>
	if(index > encryptedPins.length)
	{
		return "Index value too high";
	}
	
    index = index - 1;
    
    <%-- Get encrypted pin at requested index --%>
	var encryptedPin = encryptedPins[index];
	
	<%-- Get one time pad key cookie name at requested index --%>
	var padKeyCookieName = padKeyCookieNames[index];
	
	<%-- Extract one time pad key from cookie value --%>
    var padKey = readCookie(padKeyCookieName);
    
    <%-- Did we get one time pad key from cookie value OK? --%>
    if(padKey == -1)
    {
		<%--Return an empty string just in case the implementing developer has called this method too many times--%>
    	return "";
    }
    
	<%-- Read the cookie which contains our current digit counter --%>
	var pinIndex = readCookie("index");
	<%-- If the cookie doesn't exist then set our value to be 0--%>
	if(pinIndex == -1)
	{
		pinIndex = 0;
	}
	
    <%-- decrypt pin --%>
    var decryptedPinDigit = decryptPinDigit(encryptedPin, padKey, pinIndex);
	<%-- Increment the index and set it back as a cookie--%>
	pinIndex++;
	if(pinIndex>=encryptedPin.length)
	{
		pinIndex=-1;
		eraseCookie(padKeyCookieName);
		eraseCookie("index");
	}
	else
	{
		saveCookie("index",pinIndex);
	}
	
    return decryptedPinDigit;
}

<%--Method to return an individual PIN digit--%>
function decryptPinDigit(encryptedPin, padKey, digitIndex)
{
    var decryptedPin;
    var nextEncryptedPinDigit;
    var nextPadKeyDigit;
    var nextDecryptedPinDigit;
    
    decryptedPin = "";
    
	if(digitIndex >= encryptedPin.length || digitIndex <0)
	{
		return "Invalid PIN digit index specified";
	}
	
	encryptedPinDigit = encryptedPin.charAt(digitIndex);
    encryptedPinDigit = fromHexCharacter(encryptedPinDigit);
        
    padKeyDigit = padKey.charAt(digitIndex);
    padKeyDigit = fromHexCharacter(padKeyDigit);
        
    decryptedPinDigit = encryptedPinDigit ^ padKeyDigit;
 
    return decryptedPinDigit;
}

<%--Reads cookie with the supplied name--%>
function readCookie(name) 
{
    if(document.cookie == '') 
    { 
        return -1;
    } 
    
    var cookies = document.cookie;
    var start = cookies.indexOf(name);

    if(start != -1)
    {
        start += name.length + 1;          

        var end = cookies.indexOf(';', start);

        if(end == -1)
        {
            end = cookies.length;
        }

        return cookies.substring(start, end);
    } 
    else 
    {
         return -1;	
    }
}

function saveCookie(name,value) 
{
	var expires = "";
	var cookieDomain = "<%=padEncryptedPins.getPadKeyCookieDomain()%>";
	<%--document.cookie = name+"="+value+"; Domain="+cookieDomain+"; expires="+expires+"; path=/; Secure";--%>
	document.cookie = name+"="+value;
}

function getNumberOfPINs()
{
	return <%=padEncryptedPins.getTotalPinNumbers()%>;
}

<%--Deletes cookie with the supplied name--%>
function eraseCookie(name)
{
    var cookieValue = "abc";
    var cookieDomain = "<%=padEncryptedPins.getPadKeyCookieDomain()%>";
    var expires = "Sun, 01-Dec-1970 00:00:10 GMT";

    var cookieToErase = name + "=" + cookieValue + "; Domain=" + cookieDomain + "; expires=" + expires + "; Path=/; Secure";
    document.cookie = cookieToErase;
	cookieToErase = name+"="+cookieValue+"; expires="+expires;
	document.cookie = cookieToErase;
}

function fromHexCharacter(nibble)
{
   	var c;
   	
   	switch(nibble)
   	{
   	    case "0" :
   	    	
   	        c = 0;
   	    	
   	    	break;
   	    	
        case "1" :
   	    	
   	    	c = 1;
   	    	
   	    	break;
   	    	
        case "2" :
   	    	
   	    	c = 2;
   	    	
   	    	break;
   	    	
        case "3" :
   	    	
   	    	c = 3;
   	    	
   	    	break;
   	    	
        case "4" :
   	    	
   	    	c = 4;
   	    	
   	    	break;
   	    	
        case "5" :
   	    	
   	    	c = 5;
   	    	
   	    	break;
   	    	
        case "6" :
   	    	
   	    	c = 6;
   	    	
   	    	break;
   	    	
        case "7" :
   	    	
   	    	c = 7;
   	    	
   	    	break;
   	    	
        case "8" :
   	    	
   	    	c = 8;
   	    	
   	    	break;
   	    	
        case "9" :
   	    	
   	    	c = 9;
   	    	
   	    	break;
   	    	
        case "a" :
        case "A" :
   	    	
   	    	c = 10;
   	    	
   	    	break;
   	    	
        case "b" :
        case "B" :
   	    	
   	    	c = 11;
   	    	
   	    	break;
   	    	
        case "c" :
        case "C" :
   	    	
   	    	c = 12;
   	    	
   	    	break;
   	    	
        case "d" :
        case "D" :
   	    	
   	    	c = 13;
   	    	
   	    	break;
   	    	
        case "e" :
        case "E" :
           	    	
   	    	c = 14;
   	    	
   	    	break;
   	    	
        case "f" :
        case "F" :
           	    	
   	    	c = 15;
   	    	
   	    	break;
   	    	
   	    default :
   	    
   	        c = 0;
   	}
   	
   	return c;
}