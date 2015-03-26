package com.safenetinc.viewpin.cli.logging;

public class Constants {
	
	
	 /**
     * The tomcat webapp directory
     */
	public static final String Webapp_Directory = "/usr/tomcat/webapps/";

	 /**
     * The usr-file directory
     */
	public static final String USR_FILE_DIR = "/usr-files/";
	
	/**
     * The configuration  directory
     */
	public static final String Config_Directory = "/configuration/";
	
	/**
     * The Agent Configuration File
     */
	public static final String Logging_Config_File = "pinagent-log4j.xml";

	/**
     * The Agent Configuration File
     */
	public static final String New_Logging_Config_File = "pinagent-log4j.xml.new";
	/*
	 * name of Console appender threshold to be printed
	 */
	public static final String CONSOLE_APPENDER_THRESHOLD 		= "ConsoleAppender Threshold";
	
	/*
	 * name of File appender threshold to be printed
	 */
	public static final String FILE_APPENDER_THRESHOLD 			= "FileAppender Threshold";
	/*
	 * name of Syslog appender threshold to be printed
	 */
	public static final String SYSLOG_APPENDER_THRESHOLD 		= "SylogAppender Threshold";
	
	/*
	 * name of Syslog appender IP to be printed
	 */
	public static final String SYSLOG_APPENDER_IP 				= "SylogAppender IP";
	
	/*
	 * name of ViewPIN appender to be printed
	 */	
	public static final String VIEWPIN_APPENDER_THRESHOLD		= "ViewPINAppender Threshold";
	
	/*
	 * name of Console appender parameter 
	 */	
	public static final String CONSOLE_APPENDER_NAME 			= "stdout";
	
	/*
	 * name of File appender parameter 
	 */	
	public static final String FILE_APPENDER_NAME 				= "FILE";
	
	
	/*
	 * name of Syslog appender parameter 
	 */	
	public static final String SYSLOG_APPENDER_NAME 			= "syslogudp";
	
	/*
	 * name of Console appender parameter 
	 */	
	public static final String VIEWPIN_CATEGORY_NAME 			= "com.safenetinc.viewpin";
	 
	/*
	 *  appender tag name 
	 */
	public static final String ELEMENT_APPENDER					= "appender";

	/*
	 *  category tag name 
	 */
	public static final String ELEMENT_CATEGORY					= "category";
	 
	/*
	 *  Threshold Param name 
	 */
	public static final String THRESHOLD_VALUE 					= "threshold";
	
	/*
	 *  Syslog Param name 
	 */
	public static final String SYSLOGHOST_VALUE				 	= "syslogHost";
	
	/*
	 * param tag name 
	 */
	public static final String ELEMENT_PARAM 					= "param";

	/*
	 * priority tag name 
	 */
	public static final String ELEMENT_PRIORITY 				= "priority";
	 

	/*
	 * value attribute name 
	 */
	public static final String ATTRIBUTE_VALUE					 = "value";
	
	/*
	 * name attribute  
	 */
	public static final String ATTRIBUTE_NAME					 = "name";
	
	/*
	 * Logging Value OFF
	 */
	public static final String OFF								 = "OFF";
	
	/*
	 * Logging Value DEBUG
	 */
	public static final String DEBUG							 = "DEBUG";

	/*
	 * Logging Value FATAL
	 */
	public static final String FATAL							 = "FATAL";
	
	/*
	 * Logging Value INFO
	 */
	public static final String INFO							 	 = "INFO";
	
	/*
	 * Logging Value ERROR
	 */
	public static final String ERROR							  = "ERROR";
	
	/*
	 * Logging Value WARN
	 */
	public static final String WARN							  	  = "WARN";
	
	/*
	 * name of Log File Path
	 */	
	public static final String LOG_FILE 						  = "agent.log";
	

	
}
