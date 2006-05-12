/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.core;

/**
 * @author Mik Kersten (hardening of prototype
 */
public interface IBugzillaConstants {

	// Error response from bugzilla server upon bug request
	static final String ERROR_INVALID_BUG_ID = "InvalidBugId";

	static final String ERROR_INVALID_USERNAME_OR_PASSWORD = "Invalid Username Or Password";

	static final String MOST_RECENT_QUERY = "org.eclipse.mylar.bugzilla.query.last";

	static final String SERVER_VERSION = "org.eclipse.mylar.bugzilla.server.version";

	static final String SERVER_220 = "2.20";

	static final String SERVER_218 = "2.18";

	/** Used by search engine */
	public static final int RETURN_ALL_HITS = -1;

	public static final String CONTENT_TYPE_RDF = "&ctype=rdf";

	/** Supported bugzilla repository versions */
	static public enum BugzillaServerVersion {
		SERVER_218, SERVER_220, SERVER_222;

		@Override
		public String toString() {
			switch (this) {
			case SERVER_222:
				return "2.22";
			case SERVER_220:
				return "2.20";
			case SERVER_218:
				return "2.18";
			default:
				return "null";
			}
		}

		/** returns null if version string unknown* */
		static public BugzillaServerVersion fromString(String version) {
			if (version.equals(SERVER_222.toString()))
				return SERVER_222;
			if (version.equals(SERVER_220.toString()))
				return SERVER_220;
			if (version.equals(SERVER_218.toString()))
				return SERVER_218;
			return null;
		}
	}

	// static final String IS_216 = "BUGZILLA_IS_216";
	// static final String IS_218 = "BUGZILLA_IS_218";
	// static final String IS_220 = "BUGZILLA_IS_220";

	static final String REFRESH_QUERY = "org.eclipse.mylar.bugzilla.query.refresh";

	static final String MAX_RESULTS = "org.eclipse.mylar.bugzilla.search.results.max";

	// names for the resources used to hold the different attributes of a bug
	static final String VALUES_STATUS = "org.eclipse.mylar.bugzilla.values.status";

	static final String VALUSE_STATUS_PRESELECTED = "org.eclipse.mylar.bugzilla.values.status.preselected";

	static final String VALUES_RESOLUTION = "org.eclipse.mylar.bugzilla.values.resolution";

	static final String VALUES_SEVERITY = "org.eclipse.mylar.bugzilla.values.severity";

	static final String VALUES_PRIORITY = "org.eclipse.mylar.bugzilla.values.priority";

	static final String VALUES_HARDWARE = "org.eclipse.mylar.bugzilla.values.hardware";

	static final String VALUES_OS = "org.eclipse.mylar.bugzilla.values.os";

	static final String VALUES_PRODUCT = "org.eclipse.mylar.bugzilla.values.product";

	static final String VALUES_COMPONENT = "org.eclipse.mylar.bugzilla.values.component";

	static final String VALUES_VERSION = "org.eclipse.mylar.bugzilla.values.version";

	static final String VALUES_TARGET = "org.eclipse.mylar.bugzilla.values.target";

	static final String ECLIPSE_BUGZILLA_URL = "https://bugs.eclipse.org/bugs";

	static final String TEST_BUGZILLA_216_URL = "http://mylar.eclipse.org/bugs216";

	static final String TEST_BUGZILLA_218_URL = "http://mylar.eclipse.org/bugs218";

	static final String TEST_BUGZILLA_220_URL = "http://mylar.eclipse.org/bugs220";

	static final String TEST_BUGZILLA_2201_URL = "http://mylar.eclipse.org/bugs2201";

	static final String TEST_BUGZILLA_222_URL = "http://mylar.eclipse.org/bugs222";

	// Default values for keys

	static final String[] DEFAULT_STATUS_VALUES = { "Unconfirmed", "New", "Assigned", "Reopened", "Resolved",
			"Verified", "Closed" };

	static final String[] DEFAULT_PRESELECTED_STATUS_VALUES = { "New", "Assigned", "Reopened" };

	// static final String[] DEFAULT_RESOLUTION_VALUES = { "Fixed", "Invalid",
	// "Wontfix", "Later", "Remind", "Duplicate",
	// "Worksforme", "Moved" };

	static final String[] DEFAULT_SEVERITY_VALUES = { "blocker", "critical", "major", "normal", "minor", "trivial",
			"enhancement" };

	static final String[] DEFAULT_PRIORITY_VALUES = { "P1", "P2", "P3", "P4", "P5" };

	static final String[] DEFAULT_HARDWARE_VALUES = { "All", "Macintosh", "PC", "Power PC", "Sun", "Other" };

	static final String[] DEFAULT_OS_VALUES = { "All", "AIX Motif", "Windows 95", "Windows 98", "Windows CE",
			"Windows ME", "Windows 2000", "Windows NT", "Windows XP", "Windows All", "MacOS X", "Linux", "Linux-GTK",
			"Linux-Motif", "HP-UX", "Neutrino", "QNX-Photon", "Solaris", "Unix All", "other" };

	static final String[] DEFAULT_PRODUCT_VALUES = {};

	static final String[] DEFAULT_COMPONENT_VALUES = {};

	static final String[] DEFAULT_VERSION_VALUES = {};

	static final String[] DEFAULT_TARGET_VALUES = {};

	public static final String TITLE_MESSAGE_DIALOG = "Mylar Bugzilla Client";

	public static final String TITLE_NEW_BUG = "New Bugzilla Bug";

	public static final String MESSAGE_LOGIN_FAILURE = "Bugzilla login information or repository version incorrect";

	public static final String INVALID_2201_ATTRIBUTE_IGNORED = "EclipsebugsBugzilla2.20.1";

	public static final String VALUE_STATUS_RESOLVED = "RESOLVED";

	public static final String VALUE_STATUS_NEW = "NEW";

	public static final String VALUE_STATUS_CLOSED = "CLOSED";

	public static final String VALUE_STATUS_ASSIGNED = "ASSIGNED";

	public static final String VALUE_RESOLUTION_LATER = "LATER";

	public static enum BUGZILLA_OPERATION {
		none, accept, resolve, duplicate, reassign, reassignbycomponent, reopen, verify, close;
	}

	public static enum BUGZILLA_REPORT_STATUS {
		UNCONFIRMED, NEW, ASSIGNED, REOPENED, RESOLVED, VERIFIED, CLOSED;
	}

	public static enum BUGZILLA_REPORT_SEVERITY {
		UNCONFIRMED, NEW, ASSIGNED, REOPENED, RESOLVED, VERIFIED, CLOSED;
	}

	public static enum BUGZILLA_RESOLUTION {
		FIXED, INVALID, WONTFIX, LATER, REMIND, WORKSFORME;
	}
}
