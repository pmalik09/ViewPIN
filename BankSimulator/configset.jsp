<%@page import="java.util.ArrayList"%>
<%@page import="com.safenetinc.viewpin.banksimulator.config.BankSimulatorCustomerConfiguration"%>
<%@page import="com.safenetinc.viewpin.banksimulator.BankSimulatorUser"%>
<%@page import="org.apache.commons.configuration.XMLConfiguration"%>
<%@page import="org.apache.commons.configuration.ConfigurationException"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>
<%@page import="com.safenetinc.viewpin.authority.ExpiryDate" %>
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
	<title>SafeNet ViewPIN Demonstration System - User Added</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <style type="text/css">
<!--
.style1 {font-family: Arial, Helvetica, sans-serif}
.style3 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 24px;
	font-weight: bold;
}
.style5 {color: #FFFFFF}
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
              <td><div align="center"><span class="style3">ViewPIN Demonstration System</span></div></td>
            </tr>
          </table>
		  
		  <p>&nbsp;</p>
		  <table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
            <tr>
              <td align="left" valign="top" bgcolor="#CCCCCC"><p>&nbsp;</p>
                <p class="style1">Card Data Saved. The CVV for the card specified is <%=(String)request.getSession().getAttribute("cvv")%></p>
                <table width="95%"  border="0" align="center" cellpadding="2" cellspacing="2">
                  <tr bgcolor="#666666">
                    <td width="25%"><div align="center" class="style5">ID</div></td>
                    <td width="25%"><div align="center" class="style5">Forename</div></td>
                    <td width="25%"><div align="center" class="style5">Surname</div></td>
                    <td width="25%"><div align="center" class="style5">Password</div></td>
                    <td width="25%"><div align="center" class="style5">PAN</div></td>
                    <td width="25%"><div align="center" class="style5">Expiry Date </div></td>
                    <td width="25%"><div align="center" class="style5">Calculated CVV </div></td>
                    
                  </tr>
                  <% 
                  BankSimulatorCustomerConfiguration configuration = (BankSimulatorCustomerConfiguration)getServletContext().getAttribute("customersconfiguration");
                  for (int i =0; i< configuration.getUsers().size(); i++)
                  {
                  	BankSimulatorUser user = (BankSimulatorUser)configuration.getUsers().get(i);
                  %>
                  <tr>
                    <td><div align="center"><%=user.getId()%></div></td>
                    <td><div align="center"><%=user.getFirstname()%></div></td>
                    <td><div align="center"><%=user.getSurname()%></div></td>
                    <td><div align="center"><%=user.getPassword()%></div></td>
                    <td><div align="center"><%=user.getPrimaryAccountNumber()%></div></td>
                  </tr>
                  <%
                  }
                  %>
                </table>                <p class="style1">&nbsp;</p>
                <p class="style1">&nbsp;</p></td>
              </tr>
          </table>
  
		
		</div></td>
	  </tr>
	  <tr>
              <td><div align="center"><a href="<%=xmlconfig.getString("LoginPage(0)")%>">Login as a user</a></td>
              </tr>
	</table>

</td></tr></table>

</body>
</html>




