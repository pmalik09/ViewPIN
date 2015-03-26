package com.safenetinc.syslog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Priority 
{
	private int facility = 0;
	private int severity = 0;
	private int priority = 0;

	private Priority() 
	{
		super();
	}

	private Priority(int facility, int severity) 
	{
		super();

		setFacility(facility);
		setSeverity(severity);

		setPriority((getFacility() * 8) + getSeverity());

	}

	public static Priority getInstance(int facility, int severity) throws InvalidFacilityException, InvalidSeverityException
	{
		Priority p;

		p = null;

		if(isFacilityValid(facility) == false)
		{
			throw new InvalidFacilityException();
		}

		if(isSeverityValid(severity) == false)
		{
			throw new InvalidSeverityException();
		}

		p = new Priority(facility, severity);

		return p;
	}

	private static boolean isFacilityValid(int facility)
	{
        boolean rc;

        rc = false;

		switch(facility)
		{
			case Facility.KERNEL_MESSAGES :

			case Facility.USER_LEVEL_MESSAGES :

			case Facility.MAIL_SYSTEM :

			case Facility.SYSTEM_DAEMONS :

			case Facility.SECURITY_AUTHORISATION_ONE :

			case Facility.SYSLOGD_INTERNAL :

			case Facility.LINE_PRINTER_SUBSYSTEM :

			case Facility.NETWORK_NEWS_SUBSYSTEM :

			case Facility.UUCP_SUBSYSTEM :

			case Facility.CLOCK_DAEMON_ONE :

			case Facility.SECURITY_AUTHORISATION_TWO :

			case Facility.FTP_DAEMON :

			case Facility.NTP_SUBSYSTEM :

			case Facility.LOG_AUDIT_ONE :

			case Facility.LOG_AUDIT_TWO :

			case Facility.CLOCK_DAEMON_TWO :

			case Facility.LOCAL_USE_ZER0 :

			case Facility.LOCAL_USE_ONE :

			case Facility.LOCAL_USE_TWO :

			case Facility.LOCAL_USE_THREE :

			case Facility.LOCAL_USE_FOUR :

			case Facility.LOCAL_USE_FIVE :

			case Facility.LOCAL_USE_SIX :

			case Facility.LOCAL_USE_SEVEN :

				rc = true;

				break;

		    default :

		    	rc = false;
		}

	    return rc;
	}

	private static boolean isSeverityValid(int severity) throws InvalidSeverityException
	{
		boolean rc;

		rc = false;

		switch(severity)
		{
			case Severity.EMERGENCY :

			case Severity.ALERT :

			case Severity.CRITICAL :

			case Severity.ERROR :

			case Severity.WARNING :

			case Severity.NOTICE :

			case Severity.INFORMATIONAL :

			case Severity.DEBUG :

				rc = true;

				break;

		    default :

		    	rc = false;
		}

		return rc;
	}

	private void setFacility(int facility)
	{
		this.facility = facility;
	}

	public int getFacility()
	{
		return this.facility;
	}

	private void setSeverity(int severity)
	{
		this.severity = severity;
	}

	public int getSeverity()
	{
		return this.severity;
	}

	private void setPriority(int priority)
	{
		this.priority = priority;
	}

	public int getPriority()
	{
		return this.priority;
	}

	public byte[] toByteArray() throws IOException
	{
		ByteArrayOutputStream baos;

		baos = null;

		try
		{
			baos = new ByteArrayOutputStream();

			baos.write('<');

			baos.write(String.valueOf(getPriority()).getBytes());

			baos.write('>');
		}
		finally
		{
			if(baos != null)
			{
				baos.close();
			}
		}
		if(baos != null)
			return baos.toByteArray();
		else return null;
	}
}