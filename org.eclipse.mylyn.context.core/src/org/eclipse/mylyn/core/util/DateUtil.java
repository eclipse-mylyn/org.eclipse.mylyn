/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jul 6, 2004
 */
package org.eclipse.mylar.core.util;

import java.util.Calendar;

/**
 * @author Mik Kersten
 */
public class DateUtil {
	
    public static String getFormattedDate() {	
    	return getFormattedDate(Calendar.getInstance());
    }
    
    public static String getFormattedDate(Calendar calendar) {
        int monthInt = (calendar.get(Calendar.MONTH)+1);
        String month = "" + monthInt;
        if (monthInt < 10) month = "0" + month;
        int dateInt = (calendar.get(Calendar.DATE));
        String date = "" + dateInt;
        if (dateInt < 10) date = "0" + date;        
        return 
        	calendar.get(Calendar.YEAR) + "-" + 
            month + "-" + date;         	
    }

    public static String getFormattedTime() {
        return
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + 
            Calendar.getInstance().get(Calendar.MINUTE) + ":" + 
            Calendar.getInstance().get(Calendar.SECOND);
    }
    
    public static String getFormattedDateTime(long time) {
    	// XXX: need to get UTC times
    	Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(time);
        return
            getFormattedDate() + "-" + 
            c.get(Calendar.HOUR) + "-" +
            c.get(Calendar.MINUTE) + "-" + 
            c.get(Calendar.SECOND);
    }
    
    /** Returns the time in the format: HHH:MM */
    public static String getFormattedDurationShort(long duration){
    	if (duration <= 0){
    		return "00:00";
    	}
    	
    	long totalMinutes = duration / 1000 / 60;
    	long remainderMinutes = totalMinutes % 60;
    	long totalHours = totalMinutes / 60;
    	
    	String hourString = "" + totalHours;
    	String minuteString = "" + remainderMinutes;
    	
    	if (totalHours < 10){
    		hourString = "0" + hourString;
    	}
    	
    	if (remainderMinutes < 10){
    		minuteString = "0" + remainderMinutes;
    	}
    	
    	return hourString + ":" + minuteString;
    }
     
    public static String getFormattedDuration(long duration) {
    	long seconds = duration / 1000;
		long minutes = 0;
		long hours = 0;
//		final long SECOND = 1000;
		final long MIN = 60;
		final long HOUR = MIN * 60;
		
		String hour = "";
		String min = "";
		String sec = "";
		if (seconds >= HOUR) {
			hours = seconds / HOUR;
			if (hours == 1) {
				hour = hours + " hour ";
			} else if (hours > 1) {
				hour = hours + " hours ";
			}
			seconds -= hours * HOUR;
			
			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + " minute ";
			} else if (minutes != 1) {
				min = minutes + " minutes ";
			}
			seconds -= minutes * MIN;
			if (seconds == 1) {
				sec = seconds + " second";
			} else if (seconds > 1) {
				sec = seconds + " seconds";
			}
			return hour + min + sec;
		} else if (seconds >= MIN) {
			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + " minute ";
			} else if (minutes != 1) {
				min = minutes + " minutes ";
			}
			seconds -= minutes * MIN;
			if (seconds == 1) {
				sec = seconds + " second";
			} else if (seconds > 1) {
				sec = seconds + " seconds";
			}
			return min + sec;
		} else {
			if (seconds == 1) {
				sec = seconds + " second";
			} else if (seconds > 1) {
				sec = seconds + " seconds";
			}
			return sec;
//			return "0 minutes";
		}
    }
}
