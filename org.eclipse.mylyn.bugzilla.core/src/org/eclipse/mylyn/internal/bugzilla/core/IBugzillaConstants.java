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

package org.eclipse.mylyn.internal.bugzilla.core;

/**
 * @author Mik Kersten
 */
public interface IBugzillaConstants {

	//public static final String POST_ARGS_PASSWORD = "&Bugzilla_password=";
	//public static final String POST_ARGS_LOGIN = "GoAheadAndLogIn=1&Bugzilla_login=";

	public static final String PROPERTY_CONFIGTIMESTAMP = "org.eclipse.mylyn.tasklist.repositories.config.timestamp";

	static final String ERROR_MIDAIR_COLLISION = "collision";

	static final String ERROR_MSG_MIDAIR_COLLISION = "A mid-air collision has occurred. Please synchronize by selecting Synchronize in the task's context menu.";

	static final String ERROR_COMMENT_REQUIRED = "comment required";

	static final String ERROR_MSG_COMMENT_REQUIRED = "You have to specify a new comment when making this change. Please comment on the reason for this change.";

	static final String ERROR_INVALID_USERNAME_OR_PASSWORD = "Invalid Username or Password";

	static final String LOGGED_OUT = "logged out";

	static final String MOST_RECENT_QUERY = "org.eclipse.mylyn.bugzilla.query.last";

	static final String SERVER_VERSION = "org.eclipse.mylyn.bugzilla.server.version";

	public static final int RETURN_ALL_HITS = -1;

	public static final String CONTENT_TYPE_RDF = "&ctype=rdf";

	public static final String POST_INPUT_BUGZILLA_PASSWORD = "Bugzilla_password";

	public static final String POST_INPUT_BUGZILLA_LOGIN = "Bugzilla_login";

	public static final String POST_INPUT_BUGID = "bugid";

	public static final String POST_INPUT_ACTION = "action";

	public static final String POST_INPUT_COMMENT = "comment";

	public static final String POST_INPUT_DESCRIPTION = "description";

	public static final String POST_INPUT_DATA = "data";

	public static final String URL_POST_LOGIN = "/index.cgi";

	public static final String URL_POST_ATTACHMENT_UPLOAD = "/attachment.cgi";

	public static final String URL_BUGLIST = "/buglist.cgi";

	public static final String URL_GET_ATTACHMENT_DOWNLOAD = "/attachment.cgi?id=";

	public static final String URL_GET_ATTACHMENT_SUFFIX = "/attachment.cgi?id=";

	public static final String URL_BUG_ACTIVITY = "/show_activity.cgi?id=";

	public static final String URL_SHOW_VOTES = "/votes.cgi?action=show_bug&bug_id=";

	public static final String URL_VOTE = "/votes.cgi?action=show_user&bug_id=";

	public static final String URL_DEPENDENCY_TREE = "/showdependencytree.cgi?id=";

	public static final String URL_DEPENDENCY_GRAPH = "/showdependencygraph.cgi?id=";

	public static final String URL_GET_SHOW_BUG = "/show_bug.cgi?id=";

	public static final String URL_GET_CONFIG_RDF = "/config.cgi?ctype=rdf";

	//For including fields in the xml (XML Summary mode as they like to call it)
	//use &field=fieldname for example to only reveal the product information append &field=product
	//to exclude from the xml use excludefield=fieldname. See bugzilla QuckSearch for a list of
	//fields that can be used (repositoryurl/quicksearchhack.html).
	//If somebody knows where this is officially documented I'd appreciate it if they would post a link here
	// and on bug#161321. Thanks -relves
	// (see also: https://bugzilla.mozilla.org/show_bug.cgi?id=136603https://bugzilla.mozilla.org/show_bug.cgi?id=136603)
	public static final String URL_GET_SHOW_BUG_XML = "/show_bug.cgi?ctype=xml&excludefield=attachmentdata&id=";

	public static final String URL_GET_SHOW_BUG_XML_NOID = "/show_bug.cgi?ctype=xml&excludefield=attachmentdata";

	public static final String URL_POST_SHOW_BUG = "/show_bug.cgi";

	public static final String XML_ERROR_INVALIDBUGID = "invalidbugid";

	public static final String XML_ERROR_NOTFOUND = "notfound";

	public static final String XML_ERROR_NOTPERMITTED = "notpermitted";

	public static final String ENCODING_UTF_8 = "UTF-8";

	/** Supported bugzilla repository versions */
	static public enum BugzillaServerVersion {
		SERVER_218, SERVER_220, SERVER_222, SERVER_30;

		@Override
		public String toString() {
			switch (this) {
			case SERVER_30:
				return "3.0";
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
			if (version.equals(SERVER_30.toString())) {
				return SERVER_30;
			}
			if (version.equals(SERVER_222.toString())) {
				return SERVER_222;
			}
			if (version.equals(SERVER_220.toString())) {
				return SERVER_220;
			}
			if (version.equals(SERVER_218.toString())) {
				return SERVER_218;
			}
			return null;
		}
	}

	/**
	 * Current Stable Major Release
	 */
	public static final BugzillaServerVersion SERVER_VERSION_DEFAULT = BugzillaServerVersion.SERVER_30;

	static final String REFRESH_QUERY = "org.eclipse.mylyn.bugzilla.query.refresh";

	static final String MAX_RESULTS = "org.eclipse.mylyn.bugzilla.search.results.max";

	// names for the resources used to hold the different attributes of a bug
	static final String VALUES_STATUS = "org.eclipse.mylyn.bugzilla.values.status";

	static final String VALUES_RESOLUTION = "org.eclipse.mylyn.bugzilla.values.resolution";

	static final String VALUES_COMPONENT = "org.eclipse.mylyn.bugzilla.values.component";

	static final String VALUES_VERSION = "org.eclipse.mylyn.bugzilla.values.version";

	static final String VALUES_TARGET = "org.eclipse.mylyn.bugzilla.values.target";

	static final String ECLIPSE_BUGZILLA_URL = "https://bugs.eclipse.org/bugs";

	static final String TEST_BUGZILLA_218_URL = "http://mylyn.eclipse.org/bugs218";

	static final String TEST_BUGZILLA_220_URL = "http://mylyn.eclipse.org/bugs220";

	static final String TEST_BUGZILLA_2201_URL = "http://mylyn.eclipse.org/bugs2201";

	static final String TEST_BUGZILLA_222_URL = "http://mylyn.eclipse.org/bugs222";

	static final String TEST_BUGZILLA_30_URL = "http://mylyn.eclipse.org/bugs30";

	static final String TEST_BUGZILLA_303_URL = "http://mylyn.eclipse.org/bugs303";

	static final String TEST_BUGZILLA_31_URL = "http://mylyn.eclipse.org/bugs31";

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

	public static final String TITLE_MESSAGE_DIALOG = "Mylyn Bugzilla Connector";

	public static final String TITLE_NEW_BUG = "New Bugzilla Report";

	public static final String MESSAGE_LOGIN_FAILURE = "Bugzilla login information or repository version incorrect";

	public static final String INVALID_2201_ATTRIBUTE_IGNORED = "EclipsebugsBugzilla2.20.1";

	public static final String VALUE_STATUS_RESOLVED = "RESOLVED";

	public static final String VALUE_STATUS_NEW = "NEW";

	public static final String VALUE_STATUS_CLOSED = "CLOSED";

	public static final String VALUE_STATUS_ASSIGNED = "ASSIGNED";

	public static final String VALUE_STATUS_VERIFIED = "VERIFIED";

	public static final String VALUE_RESOLUTION_LATER = "LATER";

	public static enum BUGZILLA_REPORT_STATUS {
		UNCONFIRMED, NEW, ASSIGNED, REOPENED, RESOLVED, VERIFIED, CLOSED, SCHEDULED;
	}

	public static enum BUGZILLA_RESOLUTION_2_0 {
		FIXED, INVALID, WONTFIX, LATER, REMIND, WORKSFORME;
	}

	public static enum BUGZILLA_RESOLUTION_3_0 {
		FIXED, INVALID, WONTFIX, WORKSFORME;
	}

	public static final String ERROR_MSG_OP_NOT_PERMITTED = "The requested operation is not permitted.";

	public static final String ERROR_MSG_INVALID_BUG_ID = "Invalid bug id. The requested bug id does not exist.";

	public static final String FORM_PREFIX_BUG_218 = "Bug ";

	public static final String FORM_PREFIX_BUG_220 = "Issue ";

	public static final String FORM_POSTFIX_216 = " posted";

	public static final String FORM_POSTFIX_218 = " Submitted";

	public static final String INVALID_CREDENTIALS = "Invalid repository credentials.";

	public static final String SHOW_ACTIVITY = "/show_activity.cgi?id=";

	public static final String REPOSITORY_SETTING_SHORT_LOGIN = "bugzilla.shortLoginEnabled";

	public static final String BUGZILLA_TASK_KIND = "Bug";

	static final int MAX_URL_LENGTH = 2000;

	public static final String TIMESTAMP_NOT_AVAILABLE = "n/a";

	public static final String BUGZILLA_DEF_OS = "bugzilla.default.os";

	public static final String BUGZILLA_DEF_PLATFORM = "bugzilla.default.platform";

	public static final String BUGZILLA_LANGUAGE_SETTING = "bugzilla.languageSetting";

	public static final String DEFAULT_LANG = "en (default)";

	// Bugzilla Task Attribute Editor Types 

	public static final String EDITOR_TYPE_KEYWORDS = "bugzilla.editor.keywords";

	public static final String EDITOR_TYPE_REMOVECC = "bugzilla.editor.removecc";

	public static final String EDITOR_TYPE_VOTES = "bugzilla.editor.votes";

	public static final String ATTRIBUTE_BUGZILLA_QUERY_CUSTOM = "bugzilla.query.custom";

	// Old Tags used for migration
	public static final String TAG_BUGZILLA_QUERY = "BugzillaQuery";

	public static final String TAG_BUGZILLA_CUSTOM_QUERY = "BugzillaCustomQuery";

	public static final String CONTENT_ENCODING_GZIP = "gzip";
}
