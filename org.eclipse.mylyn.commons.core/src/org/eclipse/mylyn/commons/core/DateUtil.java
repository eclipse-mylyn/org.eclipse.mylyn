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

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;

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

	private static long MILLIS_SECOND = 1000;

	private static long MILLIS_MINUTE = 60 * MILLIS_SECOND;

	private static long MILLIS_HOUR = 60 * MILLIS_MINUTE;

	private static long MILLIS_DAY = 24 * MILLIS_HOUR;

	private static long MILLIS_WEEK = 7 * MILLIS_DAY;

	private static long MILLIS_MONTH = 4 * MILLIS_WEEK;

	private enum Period {

		MONTH(MILLIS_MONTH, "{0} month", "{0} month"), //
		WEEK(MILLIS_WEEK, "{0} week", "{0} weeks"), // 
		DAY(MILLIS_DAY, "{0} day", "{0} days"), //
		HOUR(MILLIS_HOUR, "{0} hr", "{0} hrs"), //
		MINUTE(MILLIS_MINUTE, "{0} min", "{0} mins"), //
		SECOND(MILLIS_SECOND, "{0} sec", "{0} secs");

		private final long duration;

		private final String singularLabel;

		private final String pluralLabel;

		Period(long duration, String singularLabel, String pluralLabel) {
			this.duration = duration;
			this.singularLabel = singularLabel;
			this.pluralLabel = pluralLabel;
		}

		public String toString(long time) {
			long count = time / duration;
			if (count <= 1) {
				return NLS.bind(singularLabel, count);
			} else {
				return NLS.bind(pluralLabel, count);
			}
		}

	}

	private static class PeriodString {

		private final Period period;

		private final long duration;

		private PeriodString next;

		public PeriodString(Period period, long duration) {
			Assert.isNotNull(period);
			this.period = period;
			this.duration = duration;
		}

		public void append(PeriodString next) {
			this.next = next;
		}

		@Override
		public String toString() {
			return period.toString(duration) + ((next != null) ? " " + next.toString() : ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/**
	 * @since 3.5
	 */
	public static String getRelative(long time) {
		long diff = System.currentTimeMillis() - time;
		String duration = getRelativeDuration(Math.abs(diff));
		if (duration.length() > 0) {
			if (diff > 0) {
				return NLS.bind("{0} ago", duration);
			} else {
				return NLS.bind("in {0}", duration);
			}
		}
		return "";
	}

	/**
	 * @since 3.5
	 */
	public static String getRelativeDuration(long diff) {
		PeriodString string = null;
		for (Period period : Period.values()) {
			boolean wasSet = (string != null);
			if (diff >= period.duration) {
				if (string == null) {
					string = new PeriodString(period, diff);
				} else {
					string.append(new PeriodString(period, diff));
					// do not add more than two segments
					break;
				}
				diff -= (diff / period.duration) * period.duration;
			}
			// only return more than one segment if the second segment follows the first one directly 
			if (wasSet) {
				break;
			}
		}
		return (string != null) ? string.toString() : ""; //$NON-NLS-1$
	}

}
