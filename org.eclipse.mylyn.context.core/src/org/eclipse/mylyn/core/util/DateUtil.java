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
        	
        int monthInt = (Calendar.getInstance().get(Calendar.MONTH)+1);
        String month = "" + monthInt;
        if (monthInt < 10) month = "0" + month;
        int dateInt = (Calendar.getInstance().get(Calendar.DATE));
        String date = "" + dateInt;
        if (dateInt < 10) date = "0" + date;        
        return 
            Calendar.getInstance().get(Calendar.YEAR) + "-" + 
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
}
