/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.Serializable;

/**
 * @author Frank Becker
 */
public class BugzillaVersion implements Comparable<BugzillaVersion>, Serializable {

	private static final long serialVersionUID = 2027987556171301044L;

	public final static BugzillaVersion MIN_VERSION = new BugzillaVersion("2.18"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_2_18 = new BugzillaVersion("2.18"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_2_22 = new BugzillaVersion("2.22"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_3_0 = new BugzillaVersion("3.0"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_3_2 = new BugzillaVersion("3.2"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_3_4 = new BugzillaVersion("3.4"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_3_4_7 = new BugzillaVersion("3.4.7"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_3_5 = new BugzillaVersion("3.5"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_3_6 = new BugzillaVersion("3.6"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_4_0 = new BugzillaVersion("4.0"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_4_2 = new BugzillaVersion("4.2"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_4_4 = new BugzillaVersion("4.4"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_4_5_2 = new BugzillaVersion("4.5.2"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_5_0 = new BugzillaVersion("5.0"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_5_1 = new BugzillaVersion("5.1"); //$NON-NLS-1$

	public final static BugzillaVersion BUGZILLA_HEAD = new BugzillaVersion("5.1"); //$NON-NLS-1$

	public final static BugzillaVersion MAX_VERSION = BUGZILLA_5_0;

	private final int major;

	private final int minor;

	private int micro;

	private final boolean rc;

	private final boolean plus;

	public BugzillaVersion(String version) {
		String[] segments;
		if (version == null) {
			segments = new String[0];
			rc = false;
			plus = false;
		} else {
			version = version.toUpperCase();
			rc = version.contains("RC"); //$NON-NLS-1$
			plus = version.contains("+"); //$NON-NLS-1$
			if (plus) {
				version = version.replace("+", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			segments = rc ? version.split("(\\.|([R][C]))") : version.split("\\."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		major = segments.length > 0 ? parse(segments[0]) : 0;
		minor = segments.length > 1 ? parse(segments[1]) : 0;
		micro = segments.length > 2 ? parse(segments[2]) : 0;
		if (rc) {
			micro -= 100;
		}
	}

	private int parse(String segment) {
		try {
			return segment.length() == 0 ? 0 : Integer.parseInt(getVersion(segment));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private String getVersion(String segment) {
		int n = segment.indexOf('-');
		return n == -1 ? segment : segment.substring(0, n);
	}

	public boolean isSmallerOrEquals(BugzillaVersion v) {
		return compareTo(v) <= 0;
	}

	public boolean isSmaller(BugzillaVersion v) {
		return compareTo(v) < 0;
	}

	public int compareTo(BugzillaVersion v) {
		if (major < v.major) {
			return -1;
		} else if (major > v.major) {
			return 1;
		}

		if (minor < v.minor) {
			return -1;
		} else if (minor > v.minor) {
			return 1;
		}

		if (micro < v.micro) {
			return -1;
		} else if (micro > v.micro) {
			return 1;
		}

		if (plus != v.plus) {
			if (plus) {
				return 1;
			} else {
				return -1;
			}
		}

		return 0;
	}

	public int compareMajorMinorOnly(BugzillaVersion v) {
		if (major < v.major) {
			return -1;
		} else if (major > v.major) {
			return 1;
		}

		if (minor < v.minor) {
			return -1;
		} else if (minor > v.minor) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(major));
		sb.append(".").append(Integer.toString(minor)); //$NON-NLS-1$
		if (micro > 0) {
			sb.append(".").append(Integer.toString(micro)); //$NON-NLS-1$
		} else if (micro < 0) {
			sb.append("RC").append(Integer.toString(micro + 100)); //$NON-NLS-1$
		}
		if (plus) {
			sb.append("+"); //$NON-NLS-1$
		}
		return sb.toString();
	}

}
