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
<script type='text/javascript' src='<%=xmlconfig.getString("ViewPinJavascriptURL(0)")%>'></script>
<script type="text/javascript">
function ordi(n)
{

            var s='th';

            var dum = n + '';

            var len = dum.length;

            var num = dum.charAt(len-1);

            if(num==1) s='st';

            if(num==2) s='nd';

            if(num==3) s='rd';

            if(len > 1) 
            {

                        if (dum.charAt(len-2) == 1) 
                        {

                                    var s='th';

                        }

            }

            return n+s;

}
</script>
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
              <td><div align="center"><span class="style3"></span></div></td>
            </tr>
          </table>

			<table width="800" border="0" align="center" cellpadding="5" cellspacing="0">
            <script>
           
		   for (var x=1; x <=getNumberOfPINs(); x++)
            {	
							<%--
            	document.write("<tr>");
				document.write("<td align=\"left\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style3\">Your " + ordi(x) + " PIN is:</span></div></td>");
            	document.write("<td align=\"right\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style1\">"+decryptNextPINDigit(x)+"</span></div></td>");
				document.write("<td align=\"right\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style1\">"+decryptNextPINDigit(x)+"</span></div></td>");
				document.write("<td align=\"right\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style1\">"+decryptNextPINDigit(x)+"</span></div></td>");
				document.write("<td align=\"right\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style1\">"+decryptNextPINDigit(x)+"</span></div></td>");
				document.write("<td align=\"right\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style1\">"+decryptNextPINDigit(x)+"</span></div></td>");

				document.write("</tr>");
				--%>
				document.write("<tr>");
            	document.write("<td align=\"left\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style3\">Your " + ordi(x) + " PIN is:</span></div></td>");
            	document.write("<td align=\"right\" bgcolor=\"#CCCCCC\"><div align=\"center\"><span class=\"style1\">"+decryptPIN(x)+"</span></div></td>");
            	document.write("</tr>");
				
			}
            </script>
			<tr>
              <td width="30%" align="right" valign="top" bgcolor="#CCCCCC"><p>&nbsp;</p></td>
              <td align="left" bgcolor="#CCCCCC">
              </td>
            </tr>
          </table>
</form>

</body>
