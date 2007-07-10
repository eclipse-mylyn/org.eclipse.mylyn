/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.util;

import java.util.Date;

/**
 * Provides static helper methods.
 * 
 * @author Steffen Pingel
 */
public class TracUtils {

	public static Date parseDate(long seconds) {
		return new Date(seconds * 1000l);
//		Calendar c = Calendar.getInstance();
//		c.setTimeZone(TimeZone.getTimeZone(ITracClient.TIME_ZONE));
//		c.setTimeInMillis(seconds * 1000l);
//		return c.getTime();
	}

	public static long toTracTime(Date date) {
//		Calendar c = Calendar.getInstance();
//		c.setTime(date);
//		c.setTimeZone(TimeZone.getTimeZone(ITracClient.TIME_ZONE));
//		return c.getTimeInMillis() / 1000l;
		return date.getTime() / 1000l;
	}

}
