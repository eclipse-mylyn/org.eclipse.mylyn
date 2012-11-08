/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class JSonSupportTest {

	@Test
	public void testParseDate() {
		JSonSupport json = new JSonSupport();
		Calendar c = new GregorianCalendar(2012, 0, 26, 12, 33, 11);
		c.set(Calendar.MILLISECOND, 110);
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		assertEquals(new Timestamp(c.getTimeInMillis()),
				json.getGson().fromJson("\"2012-01-26 12:33:11.110000000\"", Timestamp.class));

		c = new GregorianCalendar(2012, 10, 8, 21, 38, 35);
		c.set(Calendar.MILLISECOND, 337);
		c.setTimeZone(TimeZone.getTimeZone("UTC"));
		assertEquals(new Timestamp(c.getTimeInMillis()),
				json.getGson().fromJson("\"2012-11-08 21:38:35.337000000\"", Timestamp.class));
	}

}
