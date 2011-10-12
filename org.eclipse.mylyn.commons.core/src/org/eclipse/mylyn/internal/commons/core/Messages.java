/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.commons.core.messages"; //$NON-NLS-1$

	public static String DateUtil_ago;

	public static String DateUtil_day;

	public static String DateUtil_days;

	public static String DateUtil_hour;

	public static String DateUtil_hours;

	public static String DateUtil_in;

	public static String DateUtil_minute;

	public static String DateUtil_minutes;

	public static String DateUtil_month_multi;

	public static String DateUtil_month_single;

	public static String DateUtil_second;

	public static String DateUtil_seconds;

	public static String DateUtil_week;

	public static String DateUtil_weeks;

	public static String XMLMemento_parserConfigError;

	public static String XMLMemento_ioError;

	public static String XMLMemento_formatError;

	public static String XMLMemento_noElement;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
