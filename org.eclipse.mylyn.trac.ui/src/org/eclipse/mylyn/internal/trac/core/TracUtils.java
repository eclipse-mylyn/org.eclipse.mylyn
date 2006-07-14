/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provides static helper methods.
 * 
 * @author Steffen Pingel
 */
public class TracUtils {

	public static Date parseDate(int seconds) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone(ITracClient.TIME_ZONE));
		c.setTimeInMillis(seconds * 1000l);
		return c.getTime();
	}

}
