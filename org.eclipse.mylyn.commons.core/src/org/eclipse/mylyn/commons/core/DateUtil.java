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
			String month = "" + monthInt;
			if (monthInt < 10) {
				month = "0" + month;
			}
			int dateInt = (calendar.get(Calendar.DATE));
			String date = "" + dateInt;
			if (dateInt < 10) {
				date = "0" + date;
			}
			return calendar.get(Calendar.YEAR) + "-" + month + "-" + date;
		} catch (Exception e) {
			return "<unresolved date>";
		}
	}

	/**
	 * @return Time formatted according to: http://www.iso.org/iso/date_and_time_format
	 */
	public static String getIsoFormattedDateTime(Calendar calendar) {
		return getIsoFormattedDate(calendar) + "T" + calendar.get(Calendar.HOUR) + "-" + calendar.get(Calendar.MINUTE)
				+ "-" + calendar.get(Calendar.SECOND);
	}

	/** Returns the time in the format: HHH:MM */
	public static String getFormattedDurationShort(long duration) {
		if (duration <= 0) {
			return "00:00";
		}

		long totalMinutes = duration / 1000 / 60;
		long remainderMinutes = totalMinutes % 60;
		long totalHours = totalMinutes / 60;

		String hourString = "" + totalHours;
		String minuteString = "" + remainderMinutes;

		if (totalHours < 10) {
			hourString = "0" + hourString;
		}

		if (remainderMinutes < 10) {
			minuteString = "0" + remainderMinutes;
		}

		return hourString + ":" + minuteString;
	}

	public static String getFormattedDuration(long duration, boolean includeSeconds) {
		long seconds = duration / 1000;
		long minutes = 0;
		long hours = 0;
		// final long SECOND = 1000;
		final long MIN = 60;
		final long HOUR = MIN * 60;
		String formatted = "";

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
			formatted += hour + min;
			if (includeSeconds) {
				formatted += sec;
			}
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
			formatted += min;
			if (includeSeconds) {
				formatted += sec;
			}
		} else {
			if (seconds == 1) {
				sec = seconds + " second";
			} else if (seconds > 1) {
				sec = seconds + " seconds";
			}
			if (includeSeconds) {
				formatted += sec;
			}
		}
		return formatted;
	}
}
