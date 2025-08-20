/*******************************************************************************
 * Copyright (c) 2004, 2009 Eugene Kuleshov and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation for bug 129511
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eugene Kuleshov
 */
public class TaskKeyComparator implements Comparator<String[]> {

	private static final String MODULE_TASK_PATTERN = "(?:([A-Za-z]*[:_\\-]?)(\\d+))?"; //$NON-NLS-1$

	private static final Pattern ID_PATTERN = Pattern.compile(MODULE_TASK_PATTERN);

	public static final Pattern PATTERN = Pattern.compile(MODULE_TASK_PATTERN + "(.*)"); //$NON-NLS-1$

	public int compare2(String o1, String o2) {
		String[] a1 = split(o1);
		String[] a2 = split(o2);
		return compare(a1, a2);
	}

	@Override
	public int compare(String a1[], String a2[]) {
		if (a1[0] == null && a1[1] == null) {
			a1 = split(a1[2]);
		} else if ((a1[0] == null || a1[0].length() == 0) && a1[1] != null && a1[1].length() > 0) {
			String b1[] = splitTask(a1[1]);
			a1[0] = b1[0];
			a1[1] = b1[1];
		}

		if (a2[0] == null && a2[1] == null) {
			a2 = split(a2[2]);
		} else if ((a2[0] == null || a2[0].length() == 0) && a2[1] != null && a2[1].length() > 0) {
			String b2[] = splitTask(a2[1]);
			a2[0] = b2[0];
			a2[1] = b2[1];

		}
		return compare(a1[0], a1[1], a1[2], a2[0], a2[1], a2[2]);
	}

	private static int compare(final String component1, final String key1, final String value1, final String component2,
			final String key2, final String value2) {
		if (component1 == null && component2 != null) {
			return -1;
		}
		if (component1 != null && component2 == null) {
			return 1;
		}

		if (component1 != null && component2 != null) {
			int n = component1.compareToIgnoreCase(component2);
			if (n != 0) {
				return n;
			}

			if (key1 == null && key2 != null) {
				return -1;
			}
			if (key1 != null && key2 == null) {
				return 1;
			}

			if (key1 != null && key2 != null) {
				if (key1.length() == key2.length() || key1.length() == 0 || key2.length() == 0) {
					n = key1.compareTo(key2);
				} else {
					try {
						n = Long.valueOf(key1).compareTo(Long.valueOf(key2));
					} catch (NumberFormatException e) {
						// The number was probably longer than an Long, so just compare them as text
						n = key1.compareTo(key2);
					}
				}
				if (n != 0) {
					return n;
				}
			}
		}

		return value1.compareToIgnoreCase(value2);
	}

	public String[] split(String s) {
		Matcher matcher = PATTERN.matcher(s);

		if (!matcher.find()) {
			return new String[] { null, null, s };
		}

		int n = matcher.groupCount();
		String[] res = new String[n];
		for (int i = 1; i < n + 1; i++) {
			res[i - 1] = matcher.group(i);
		}
		return res;
	}

	private static String[] splitTask(final String s) {
		Matcher matcher = ID_PATTERN.matcher(s);

		if (!matcher.find()) {
			return new String[] { null, s };
		}

		int n = matcher.groupCount();
		String[] res = new String[n];
		for (int i = 1; i < n + 1; i++) {
			res[i - 1] = matcher.group(i);
		}
		return res;
	}

}