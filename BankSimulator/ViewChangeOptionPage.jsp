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
</head>

<body>

<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%"><tr><td align="center" valign="middle">

	<table width="800" border="0" align="center" valign="middle" cellpadding="0" cellspacing="0">
	  <tr>
		<td height="591" valign="top"><div align="left">
		  <table width="100%"  border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="30%"><img src="logo.jpg" width="200" height="73" align="top"></td>
              <td>
			  <div align="center"><span class="style3">ViewPIN Demonstration System</span></div></td>
            </tr>

          </table>
		  
		  <p>&nbsp;</p>

		
		  <table width="600" border="0" align="center" cellpadding="5" cellspacing="0">
		     <form action="<%=xmlconfig.getString("PinRequestPage(0)")%>" method="POST" >
			 <tr>
				<td colspan="2" align="center"  bgcolor="#CCCCCC">
					   <input name="ViewPIN" value="ViewPIN" type="submit">
				</td>
			</tr>
			</form>
			
			<form action="<%=xmlconfig.getString("CVVVerifyPage(0)")%>" method="POST" >
			 <tr>
				<td colspan="2" align="center"  bgcolor="#CCCCCC">
					   <input name="VerifyCVV" value="VerifyCVV" type="submit">
				</td>
			</tr>
			</form>
		  </table>

		  <form action="<%=xmlconfig.getString("selectPinActionLocation(0)")%>" method="POST" >
		   <table width="600" border="0" align="center" cellpadding="5" cellspacing="0">
           <tr>
			 <td colspan="2" align="center"  bgcolor="#CCCCCC">
					   <input name="ChangePIN"  value="ChangePIN" type="submit">
				</td>
            </tr>
           </form>
          </table>
		
		</div></td>
	  </tr>
</body>
</html>




