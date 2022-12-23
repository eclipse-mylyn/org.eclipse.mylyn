/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.Serializable;

/**
 * @author Frank Becker
 */
public class BugzillaRestVersion implements Comparable<BugzillaRestVersion>, Serializable {

	private static final long serialVersionUID = 2027987556171301044L;

	public final static BugzillaRestVersion MIN_VERSION = new BugzillaRestVersion("4.5.1+"); //$NON-NLS-1$

	public final static BugzillaRestVersion BUGZILLA_4_5_1 = new BugzillaRestVersion("4.5.1+"); //$NON-NLS-1$

	public final static BugzillaRestVersion BUGZILLA_5_0 = new BugzillaRestVersion("5.0"); //$NON-NLS-1$

	public final static BugzillaRestVersion MAX_VERSION = BUGZILLA_5_0;

	private final int major;

	private final int minor;

	private int micro;

	private final boolean rc;

	private final boolean plus;

	public BugzillaRestVersion(String version) {
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

	public boolean isSmallerOrEquals(BugzillaRestVersion v) {
		return compareTo(v) <= 0;
	}

	public boolean isSmaller(BugzillaRestVersion v) {
		return compareTo(v) < 0;
	}

	public int compareTo(BugzillaRestVersion v) {
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

	public int compareMajorMinorOnly(BugzillaRestVersion v) {
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
			sb.append("rc").append(Integer.toString(micro + 100)); //$NON-NLS-1$
		}
		if (plus) {
			sb.append("+"); //$NON-NLS-1$
		}
		return sb.toString();
	}

}
