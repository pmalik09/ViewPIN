package com.safenetinc.viewpin.cli.logging;

public class LoggingConfigurationElements {
	
	private static String consoleAppenderThreshold;
	
	private static String fileAppenderThreshold;
	
	private static String syslogAppenderIP;
	
	private static String syslogAppenderThreshold;
	
	private static String viewPINThreshold;
	
	public LoggingConfigurationElements()
	{
		this.consoleAppenderThreshold = null;
		this.fileAppenderThreshold = null;
		this.syslogAppenderIP = null;
		this.syslogAppenderThreshold = null;
		
	}
	/*
	 * get and set methods
	 * for consoleAppenderThreshold
	 */
	public void setConsoleAppenderThreshold(String consoleAppender)
	{
		this.consoleAppenderThreshold = consoleAppender;
	}
	public String getConsoleAppenderThreshold()
	{
		return this.consoleAppenderThreshold;
	}
	
	/*
	 * get and set methods
	 * for consoleAppenderThreshold
	 */
	public void setFileAppenderThreshold(String fileAppender)
	{
		this.fileAppenderThreshold = fileAppender;
	}
	public String getFileAppenderThreshold()
	{
		return this.fileAppenderThreshold;
	}
	
	/*
	 * get and set methods
	 * for syslogAppenderThreshold
	 */
	public void setSyslogAppenderThreshold(String syslogAppender)
	{
		this.syslogAppenderThreshold = syslogAppender;
	}
	public String getSyslogAppenderThreshold()
	{
		return this.syslogAppenderThreshold;
	}

	/*
	 * get and set methods
	 * for syslogAppenderIP
	 */
	 public void setSyslogAppenderIP(String syslogAppenderIP)
	 {
		this.syslogAppenderIP = syslogAppenderIP;
	 }
	 public String getsyslogAppenderIP()
	 {
		return this.syslogAppenderIP;
	 }

	 /*
	 * get and set methods
	 * for ViewPIN Threshold
	 */
	 public void setViewPINThreshold(String ViewPinThreshold)
	 {
		this.viewPINThreshold = ViewPinThreshold;
	 }
	 public String getViewPINThreshold()
	 {
		return this.viewPINThreshold;
	 }
		
	

}
