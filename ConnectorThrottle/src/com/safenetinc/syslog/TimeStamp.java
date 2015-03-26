package com.safenetinc.syslog;

import java.util.Date;
import java.util.GregorianCalendar;

public class TimeStamp 
{
	private Date timeStamp = null;
	private String encodedTimeStamp = null;

	public TimeStamp(Date timeStamp) 
	{
		super();

		setTimeStamp(timeStamp);

		setEncodedTimeStamp(encodeTimeStamp(getTimeStamp()));
	}

	private void setTimeStamp(Date timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public Date getTimeStamp()
	{
		return this.timeStamp;
	}

	private String encodeTimeStamp(Date timeStamp)
	{
		StringBuffer encodedTimeStamp;

		encodedTimeStamp = null;

		encodedTimeStamp = new StringBuffer();

		// Encode month
		encodedTimeStamp.append(encodeMonth(getTimeStamp()));

		// Place space chracter between month and day
		encodedTimeStamp.append(" ");

		// Encode day
		encodedTimeStamp.append(encodeDay(getTimeStamp()));

		// Place space chracter between day and time
		encodedTimeStamp.append(" ");

		// Encode time
		encodedTimeStamp.append(encodeTime(getTimeStamp()));

		return encodedTimeStamp.toString();
	}

	private String encodeMonth(Date date)
	{
		String encodedMonth;
		GregorianCalendar gc;

		encodedMonth = null;
		gc = null;

		gc = new GregorianCalendar();

		gc.setTime(date);

		switch(gc.get(GregorianCalendar.MONTH))
		{
			case GregorianCalendar.JANUARY :

				encodedMonth = "Jan";

				break;

			case GregorianCalendar.FEBRUARY :

				encodedMonth = "Feb";

				break;

			case GregorianCalendar.MARCH :

				encodedMonth = "Mar";

				break;

			case GregorianCalendar.APRIL :

				encodedMonth = "Apr";

				break;

			case GregorianCalendar.MAY :

				encodedMonth = "May";

				break;

			case GregorianCalendar.JUNE :

				encodedMonth = "Jun";

				break;

			case GregorianCalendar.JULY :

				encodedMonth = "Jul";

				break;

			case GregorianCalendar.AUGUST :

				encodedMonth = "Aug";

				break;

			case GregorianCalendar.SEPTEMBER :

				encodedMonth = "Sep";

				break;

			case GregorianCalendar.OCTOBER :

				encodedMonth = "Oct";

				break;

			case GregorianCalendar.NOVEMBER :

				encodedMonth = "Nov";

				break;

			case GregorianCalendar.DECEMBER :

				encodedMonth = "Dec";

				break;

				default :

					break;
		}

		return encodedMonth;
	}

	private String encodeDay(Date date)
	{
		String encodedDay;
		GregorianCalendar gc;
		int day;

		encodedDay = null;
		gc = null;
		day = 0;

		gc = new GregorianCalendar();
		gc.setTime(date);

		day = gc.get(GregorianCalendar.DAY_OF_MONTH);

		if(day < 10)
		{
			encodedDay = " " + day;
		}
		else
		{
			encodedDay = "" + day;
		}

		return encodedDay;
	}

	private String encodeTime(Date date)
	{
		StringBuffer encodedTime;
		GregorianCalendar gc;
		int hour;
		int minute;
		int second;

		encodedTime = null;
		gc = null;
		hour = 0;
		minute = 0;
		second = 0;

		encodedTime = new StringBuffer();
		gc = new GregorianCalendar();
		gc.setTime(date);

		hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
		minute = gc.get(GregorianCalendar.MINUTE);
		second = gc.get(GregorianCalendar.SECOND);

		if(hour < 10)
		{
			encodedTime.append("0");
		}

		encodedTime.append("" + hour);

		encodedTime.append(":");

		if(minute < 10)
		{
			encodedTime.append("0");
		}

        encodedTime.append("" + minute);

		encodedTime.append(":");

		if(second < 10)
		{
			encodedTime.append("0");
		}

		encodedTime.append("" + second);

		return encodedTime.toString();
	}

	private void setEncodedTimeStamp(String encodedTimeStamp)
	{
		this.encodedTimeStamp = encodedTimeStamp;
	}

	public String getEncodedTimeStamp()
	{
		return this.encodedTimeStamp;
	}
}