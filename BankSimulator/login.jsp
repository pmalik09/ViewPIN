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

<html>
<head>
<title>SafeNet ViewPIN Demonstration System - Login</title>
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
              <td><div align="center"><span class="style3">Please Login to Safenet Banking</span></div></td>
            </tr>
          </table>

<form action="<%=xmlconfig.getString("LoginActionLocation(0)")%>" method="POST">
<table width="600" border="0" align="center" cellpadding="5" cellspacing="0">
            <tr>
              <td align="right" valign="top" bgcolor="#CCCCCC">&nbsp;</td>
              <td align="left" bgcolor="#CCCCCC">&nbsp;</td>
            </tr>
            <tr>
              <td align="right" valign="top" bgcolor="#CCCCCC"><span class="style1">ID:</span></td>
              <td align="left" bgcolor="#CCCCCC"><input name="id" type="text" maxlength="6"></td>
            </tr>
            <tr>
              <td align="right" valign="top" bgcolor="#CCCCCC"><span class="style1">Password:</span></td>
              <td align="left" bgcolor="#CCCCCC"><input name="password" type="password"/></td>
            </tr>
			<tr>
              <td width="30%" align="right" valign="top" bgcolor="#CCCCCC"><p>&nbsp;</p></td>
              <td align="left" bgcolor="#CCCCCC">
              <p align="center">
              <input name="submit" value="submit" type="submit">
              </p>
              </td>
            </tr>
            <tr>
            <td></td>
            </tr>
            <tr>
            <td align="right" valign="top"><span class="style1">
            <a href="config.html">Add a user</a>
            </td>
            </tr>
          </table>
</form>

</body>


