/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import java.util.Calendar;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.commons.core.Messages;
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
			int monthInt = calendar.get(Calendar.MONTH) + 1;
			String month = "" + monthInt; //$NON-NLS-1$
			if (monthInt < 10) {
				month = "0" + month; //$NON-NLS-1$
			}
			int dateInt = calendar.get(Calendar.DATE);
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
	 * @return Time formatted according to: https://www.iso.org/iso/date_and_time_format
	 */
	public static String getIsoFormattedDateTime(Calendar calendar) {
		return getIsoFormattedDate(calendar) + "T" + calendar.get(Calendar.HOUR) + "-" + calendar.get(Calendar.MINUTE) //$NON-NLS-1$ //$NON-NLS-2$
				+ "-" + calendar.get(Calendar.SECOND); //$NON-NLS-1$
	}

	/**
	 * Returns the time in the format: HHH:MM. If <code>includeSeconds</code> is true, the returned format is: HHH:MM:SS.
	 * 
	 * @since 3.5
	 */
	public static String getFormattedDurationShort(long duration, boolean includeSeconds) {
		if (duration <= 0) {
			return "00:00"; //$NON-NLS-1$
		}

		long totalSeconds = duration / 1000;
		long remainderSeconds = totalSeconds % 60;
		long totalMinutes = totalSeconds / 60;
		long remainderMinutes = totalMinutes % 60;
		long totalHours = totalMinutes / 60;

		StringBuilder sb = new StringBuilder(8);
		if (totalHours < 10) {
			sb.append("0"); //$NON-NLS-1$
		}
		sb.append(totalHours);
		sb.append(":"); //$NON-NLS-1$
		if (remainderMinutes < 10) {
			sb.append("0"); //$NON-NLS-1$
		}
		sb.append(remainderMinutes);
		if (includeSeconds) {
			sb.append(":"); //$NON-NLS-1$
			if (remainderSeconds < 10) {
				sb.append("0"); //$NON-NLS-1$
			}
			sb.append(remainderSeconds);
		}
		return sb.toString();
	}

	/** Returns the time in the format: HHH:MM */
	public static String getFormattedDurationShort(long duration) {
		return getFormattedDurationShort(duration, false);
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
			} else {
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
			} else {
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

		MONTH(MILLIS_MONTH, Messages.DateUtil_month_single, Messages.DateUtil_month_multi), //
		WEEK(MILLIS_WEEK, Messages.DateUtil_week, Messages.DateUtil_weeks), //
		DAY(MILLIS_DAY, Messages.DateUtil_day, Messages.DateUtil_days), //
		HOUR(MILLIS_HOUR, Messages.DateUtil_hour, Messages.DateUtil_hours), //
		MINUTE(MILLIS_MINUTE, Messages.DateUtil_minute, Messages.DateUtil_minutes), //
		SECOND(MILLIS_SECOND, Messages.DateUtil_second, Messages.DateUtil_seconds);

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
			return period.toString(duration) + (next != null ? " " + next.toString() : ""); //$NON-NLS-1$ //$NON-NLS-2$
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
				return NLS.bind(Messages.DateUtil_ago, duration);
			} else {
				return NLS.bind(Messages.DateUtil_in, duration);
			}
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @since 3.5
	 */
	public static String getRelativeDuration(long diff) {
		PeriodString string = null;
		for (Period period : Period.values()) {
			boolean wasSet = string != null;
			if (diff >= period.duration) {
				if (string == null) {
					string = new PeriodString(period, diff);
				} else {
					string.append(new PeriodString(period, diff));
					// do not add more than two segments
					break;
				}
				diff -= diff / period.duration * period.duration;
			}
			// only return more than one segment if the second segment follows the first one directly
			if (wasSet) {
				break;
			}
		}
		return string != null ? string.toString() : ""; //$NON-NLS-1$
	}

}
