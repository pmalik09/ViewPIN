<%@page import="org.apache.commons.configuration.XMLConfiguration"%>
<%@page import="org.apache.commons.configuration.ConfigurationException"%>
<%@page import="java.io.File"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.ArrayList"%>

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
<script language=JavaScript>
function getSelectedValue()
{
	
	var element = document.getElementById("selectBox1");

	var changetag = document.getElementById("changeit");

	var selIndex = element.selectedIndex; 
	changetag.value = selIndex;
	return selIndex;
}

</script>
</head>
<body>
	<table width="600" border="0" align="center" valign="middle" cellpadding="0" cellspacing="0" height="400">
			<tr>
				<td  valign="top"><div align="left">
				  <table width="100%"  border="0" cellspacing="0" cellpadding="0">
				    <tr>
						<td width="30%"><img src="logo.jpg" width="200" height="73" align="top"></td>
			            <td><div align="center"><span class="style3">ViewPIN+ Demonstration System</span></div></td>
		            </tr>
				  </table>
		  
						 <p>&nbsp;</p>

			
				<table width="600" border="0" align="center" valign="top" cellpadding="0" cellspacing="0" height="100">
			
				 <form name= "changePIN" action="<%=xmlconfig.getString("ChangePinActionLocation(0)")%>" method="POST" onSubmit="getSelectedValue()">
					</tr>
							<tr>
							 <td width="100%" align="center" bgcolor="#CCCCCC"><span class="style1">Select PIN ID To Be Changed</span></td>
							<td>
							<tr>
							<tr>
							 <td width="100%" align="center" bgcolor="#CCCCCC"><span class="style1">
							 	
							<SELECT name="selectPIN" id="selectBox1" colspan="2" class="style1" >
						
							<%
							  String PINNo;
							  
							  int j =0;
							  ArrayList<String> PINLIST = (ArrayList<String>)request.getSession().getAttribute("pinList");
							  for(int i=0; i<PINLIST.size();i++)
							  {
								  j = i+1 ;
								  %>
							
								 
									<OPTION ><%=j%>
																	
							<%
							  }
							%>		
							</SELECT> 
							<input name="change" type="submit" value="Change"  >
							
							<td>
							<input name="changeid" type="hidden" id="changeit" >
								
							

					</form>
					</td>
					</tr>
				</table>
	
						
 
		</div></td>
	  </tr>
	</table>
</body>
</html>




