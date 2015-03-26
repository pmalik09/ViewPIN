<%@page import="org.apache.commons.configuration.XMLConfiguration"%>
<%@page import="org.apache.commons.configuration.ConfigurationException"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>

<%
		XMLConfiguration xmlconfig = new XMLConfiguration();
		
		URL configurationFile = Thread.currentThread().getContextClassLoader().getResource("/BankSimulatorConfig.xml");
		
        try
        {
            xmlconfig.load(configurationFile);
        }
        catch(ConfigurationException ce)
        {
            System.out.println("Error - Unable to load BankSimulatorConfig.xml configuration file");
            ce.printStackTrace();
        }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
	<title>SafeNet ViewPIN Demonstration System</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <style type="text/css">
<!--
.style1 {font-family: Arial, Helvetica, sans-serif}
.style3 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 24px;
	font-weight: bold;
}
-->
</style>

<script type="text/javascript">
function validate (form)
{
	var elem = form.elements;
	if(elem.expirydatemonth.value < 1) {
		alert("Please check your expiry date month value,should not below than 1");
		return false;
	}
	
	if(elem.expirydatemonth.value > 12) {
		alert("Please check your expiry date month value,should not exceed 12");
		return false;
	}
	return true;
}
</script>

</head>

<body>


	<table width="600" border="0" align="center" valign="middle" cellpadding="0" cellspacing="0" height="300">
		<tr>
			<td  valign="top"><div align="left">
			  <table width="100%"  border="0" cellspacing="0" cellpadding="0">
			    <tr>
					<td width="30%"><img src="logo.jpg" width="200" height="73" align="top"></td>
		            <td><div align="center"><span class="style3">ViewPIN+ Demonstration System</span></div></td>
		        </tr>
			  </table>
		  
			 <p>&nbsp;</p>

			
						
			<table width="400" border="0" align="center" valign="top" cellpadding="0" cellspacing="0" height="300">
				</tr>
					<tr>
					  <td  colspan ="2" bgcolor="#CCCCCC" align="center"><p><strong>View PIN Form</strong><br></p></td>
					  <form action="<%=xmlconfig.getString("RequestPostURL(0)")%>" ,="" method="post" onsubmit="return validate(this)">
					</tr>
						<tr>
						  <td width="50%" align="right"  bgcolor="#CCCCCC"><span class="style1">CVV:</span></td>
							<td align="left" bgcolor="#CCCCCC"> <input name="cardholderverificationvalue" type="text" maxlength="3">
						</tr>
						<tr>
							<td width="50%" align="right"  bgcolor="#CCCCCC"><span class="style1">Expiry Date Month (MM):</span></td>
							<td align="left" bgcolor="#CCCCCC"> <input name="expirydatemonth" type="text" maxlength="2">
						</tr>
						<tr>
							 <td width="50%" align="right"  bgcolor="#CCCCCC"><span class="style1">Expiry Date Year (YY):</span></td>
						     <td align="left" bgcolor="#CCCCCC"> <input name="expirydateyear" type="text" maxlength="2">
						</tr>
						<tr>
							 <td width="50%" align="right" bgcolor="#CCCCCC"><span class="style1">PAN (16 digits):</span></td>
							 <td align="left" bgcolor="#CCCCCC"> <input name="primaryaccountnumber" type="text" maxlength="16">
						</tr>
						<tr>
							<td colspan="2" align="center"  bgcolor="#CCCCCC">
							<input name="authoritysubjectkeyidentifier" type="hidden", value="<%=xmlconfig.getString("PinAuthorityWrappingCertificateSubjectKeyIdentifier(0)")%>">
							<input name="authorityname" type="hidden", value="<%=xmlconfig.getString("PinAuthorityCVV(0)")%>">
							<input name="requesttype" type="hidden", value=02>
							<input name="submit" value="submit" type="submit">
					</form>
					</td>
					</tr>
					</tr>
					</table>
	
						
 
		</div></td>
	  </tr>
	</table>
</body>
</html>