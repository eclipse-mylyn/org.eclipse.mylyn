/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import java.util.Calendar;

/**
 * Used for formatting dates.
 * 
 * @author Mik Kersten
 * @since 3.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class DateUtil {

	public static String getIsoFormattedDate(Calendar calendar) {
		try {
			int monthInt = (calendar.get(Calendar.MONTH) + 1);
			String month = "" + monthInt; //$NON-NLS-1$
			if (monthInt < 10) {
				month = "0" + month; //$NON-NLS-1$
			}
			int dateInt = (calendar.get(Calendar.DATE));
			String date = "" + dateInt; //$NON-NLS-1$
			if (dateInt < 10) {
				date = "0" + date; //$NON-NLS-1$
			}
			return calendar.get(Calendar.YEAR) + "-" + month + "-" + date; //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			return "<unresolved date>"; //$NON-NLS-1$
		}
	}

	/**
	 * @return Time formatted according to: http://www.iso.org/iso/date_and_time_format
	 */
	public static String getIsoFormattedDateTime(Calendar calendar) {
		return getIsoFormattedDate(calendar) + "T" + calendar.get(Calendar.HOUR) + "-" + calendar.get(Calendar.MINUTE) //$NON-NLS-1$ //$NON-NLS-2$
				+ "-" + calendar.get(Calendar.SECOND); //$NON-NLS-1$
	}

	/** Returns the time in the format: HHH:MM */
	public static String getFormattedDurationShort(long duration) {
		if (duration <= 0) {
			return "00:00"; //$NON-NLS-1$
		}

		long totalMinutes = duration / 1000 / 60;
		long remainderMinutes = totalMinutes % 60;
		long totalHours = totalMinutes / 60;

		String hourString = "" + totalHours; //$NON-NLS-1$
		String minuteString = "" + remainderMinutes; //$NON-NLS-1$

		if (totalHours < 10) {
			hourString = "0" + hourString; //$NON-NLS-1$
		}

		if (remainderMinutes < 10) {
			minuteString = "0" + remainderMinutes; //$NON-NLS-1$
		}

		return hourString + ":" + minuteString; //$NON-NLS-1$
	}

	/**
	 * @deprecated The result of this method is not properly localized.
	 */
	@Deprecated
	public static String getFormattedDuration(long duration, boolean includeSeconds) {
		long seconds = duration / 1000;
		long minutes = 0;
		long hours = 0;
		// final long SECOND = 1000;
		final long MIN = 60;
		final long HOUR = MIN * 60;
		String formatted = ""; //$NON-NLS-1$

		String hour = ""; //$NON-NLS-1$
		String min = ""; //$NON-NLS-1$
		String sec = ""; //$NON-NLS-1$
		if (seconds >= HOUR) {
			hours = seconds / HOUR;
			if (hours == 1) {
				hour = hours + " hour "; //$NON-NLS-1$
			} else if (hours > 1) {
				hour = hours + " hours "; //$NON-NLS-1$
			}
			seconds -= hours * HOUR;

			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + " minute "; //$NON-NLS-1$
			} else if (minutes != 1) {
				min = minutes + " minutes "; //$NON-NLS-1$
			}
			seconds -= minutes * MIN;
			if (seconds == 1) {
				sec = seconds + " second"; //$NON-NLS-1$
			} else if (seconds > 1) {
				sec = seconds + " seconds"; //$NON-NLS-1$
			}
			formatted += hour + min;
			if (includeSeconds) {
				formatted += sec;
			}
		} else if (seconds >= MIN) {
			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + " minute "; //$NON-NLS-1$
			} else if (minutes != 1) {
				min = minutes + " minutes "; //$NON-NLS-1$
			}
			seconds -= minutes * MIN;
			if (seconds == 1) {
				sec = seconds + " second"; //$NON-NLS-1$
			} else if (seconds > 1) {
				sec = seconds + " seconds"; //$NON-NLS-1$
			}
			formatted += min;
			if (includeSeconds) {
				formatted += sec;
			}
		} else {
			if (seconds == 1) {
				sec = seconds + " second"; //$NON-NLS-1$
			} else if (seconds > 1) {
				sec = seconds + " seconds"; //$NON-NLS-1$
			}
			if (includeSeconds) {
				formatted += sec;
			}
		}
		return formatted;
	}
}
