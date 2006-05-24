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
package org.eclipse.mylar.internal.bugzilla.ui.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.AbstractReportFactory.UnrecognizedBugzillaError;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.WebBrowserDialog;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.PlatformUI;

/**
 * Queries the Bugzilla server for the list of bugs matching search criteria.
 * 
 * @author Mik Kersten (hardening of initial prototype)
 */
public class BugzillaSearchEngine {

	protected static final String QUERYING_SERVER = "Querying Bugzilla Server...";

	/**
	 * regular expression matching values of query matches' attributes in
	 * Eclipse.org Bugzilla
	 */
	public static final Pattern reValue = Pattern.compile("<td><nobr>([^<]*)</nobr>");

	public static final Pattern reValueBugzilla220 = Pattern.compile("<td style=\"white-space: nowrap\">([^<]*)");

	private Proxy proxySettings;

	private String urlString;

	private TaskRepository repository;

	private boolean maxReached = false;

	public BugzillaSearchEngine(TaskRepository repository, String queryUrl, Proxy proxySettings) {
		urlString = queryUrl;
		urlString = urlString.concat(IBugzillaConstants.CONTENT_TYPE_RDF);
		this.repository = repository;
		this.proxySettings = proxySettings;
		 if (repository.hasCredentials()) {
			try {
				urlString = BugzillaRepositoryUtil.addCredentials(urlString, repository.getUserName(), repository.getPassword());
			} catch (UnsupportedEncodingException e) {
				/*
				 * Do nothing. Every implementation of the Java platform is
				 * required to support the standard charset "UTF-8"
				 */
			}
		}

	}

	/**
	 * Wrapper for search
	 * 
	 * @param collector -
	 *            The collector for the results to go into
	 */
	public IStatus search(IBugzillaSearchResultCollector collector) throws LoginException {
		return this.search(collector, 0, IBugzillaConstants.RETURN_ALL_HITS);
	}

	/**
	 * Wrapper for search
	 * 
	 * @param collector -
	 *            The collector for the results to go into
	 * @param startMatches -
	 *            The number of matches to start with for the progress monitor
	 */
	public IStatus search(IBugzillaSearchResultCollector collector, int startMatches) throws LoginException {
		return this.search(collector, startMatches, BugzillaUiPlugin.getDefault().getMaxResults());
	}

	/**
	 * Executes the query, parses the response, and adds hits to the search
	 * result collector.
	 * 
	 * @param collector -
	 *            The collector for the search results
	 * @param startMatches -
	 *            The number of matches to start with for the progress monitor
	 * @param maxHits -
	 *            the maximum number of matches to return or
	 *            IBugzillaConstants.RETURN_ALL_HITS for unlimited
	 */
	public IStatus search(IBugzillaSearchResultCollector collector, int startMatches, int maxHits)
			throws LoginException {
		IProgressMonitor monitor = collector.getProgressMonitor();
		IStatus status = null;
		boolean possibleBadLogin = false;
		int numCollected = 0;
		BufferedReader in = null;

		try {
			monitor.beginTask(QUERYING_SERVER, maxHits);// IProgressMonitor.UNKNOWN
			collector.aboutToStart(startMatches);

			if (monitor.isCanceled()) {
				throw new OperationCanceledException("Search cancelled");
			}
			RepositoryQueryResultsFactory queryFactory = RepositoryQueryResultsFactory.getInstance();
			queryFactory.performQuery(repository.getUrl(), collector, urlString, proxySettings, maxHits, repository
					.getCharacterEncoding());

			// URLConnection cntx =
			// BugzillaPlugin.getDefault().getUrlConnection(new URL(urlString),
			// proxySettings);
			// if (cntx == null || !(cntx instanceof HttpURLConnection)) {
			// return null;
			// }
			//
			// HttpURLConnection connection = (HttpURLConnection) cntx;
			// connection.connect();
			// int responseCode = connection.getResponseCode();
			// if (responseCode != HttpURLConnection.HTTP_OK) {
			// String msg;
			// if (responseCode == -1 || responseCode ==
			// HttpURLConnection.HTTP_FORBIDDEN)
			// msg = repository.getUrl()
			// + " does not seem to be a valid Bugzilla server. Check Bugzilla
			// preferences.";
			// else
			// msg = "HTTP Error " + responseCode + " (" +
			// connection.getResponseMessage()
			// + ") while querying Bugzilla Server. Check Bugzilla
			// preferences.";
			//
			// throw new BugzillaException(msg);
			// }
			//
			// if (monitor.isCanceled()) {
			// throw new OperationCanceledException("Search cancelled");
			// }
			//			
			//			
			// if (characterEncoding != null) {
			// in = new BufferedReader(new
			// InputStreamReader(connection.getInputStream(),
			// characterEncoding));
			// } else {
			// in = new BufferedReader(new
			// InputStreamReader(connection.getInputStream()));
			// }
			//			
			// if (monitor.isCanceled()) {
			// throw new OperationCanceledException("Search cancelled");
			// }
			//
			// SaxBugzillaQueryContentHandler contentHandler = new
			// SaxBugzillaQueryContentHandler(repository, collector,
			// maxMatches);
			//
			// try {
			// XMLReader reader = XMLReaderFactory.createXMLReader();
			// reader.setContentHandler(contentHandler);
			// reader.setErrorHandler(new ErrorHandler() {
			//
			// public void error(SAXParseException exception) throws
			// SAXException {
			// MylarStatusHandler.fail(exception, "Mylar: BugzillaSearchEngine
			// Sax parser error", false);
			// }
			//
			// public void fatalError(SAXParseException arg0) throws
			// SAXException {
			// // ignore
			//
			// }
			//
			// public void warning(SAXParseException exception) throws
			// SAXException {
			// // ignore
			//
			// }
			// });
			// reader.parse(new InputSource(in));
			//
			// // if (contentHandler.errorOccurred()) {
			// // throw new IOException(contentHandler.getErrorMessage());
			// // }
			//
			// } catch (SAXException e) {
			// throw new IOException(e.getMessage());
			// }

		} catch (CoreException e) {
			status = new MultiStatus(BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR,
					"Core Exception occurred while querying Bugzilla Server " + repository.getUrl() + ".\n"
							+ "\nClick Details for more information.", e);
			((MultiStatus) status).add(e.getStatus());

			// write error to log
			BugzillaPlugin.log(status);
		} catch (OperationCanceledException e) {
			status = new Status(IStatus.CANCEL, BugzillaUiPlugin.PLUGIN_ID, IStatus.CANCEL, "", null);
		} catch (LoginException e) {
			status = new MultiStatus(BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR,
					"Login error occurred while querying Bugzilla Server " + repository.getUrl() + ".\n"
							+ "\nEnsure proper configuration in "+TaskRepositoriesView.NAME+".", e);

			IStatus s = new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
					+ ":  ", e);
			((MultiStatus) status).add(s);
			s = new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "search failed", e);
			((MultiStatus) status).add(s);
		} catch (final UnrecognizedBugzillaError e) {
			
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					WebBrowserDialog.openAcceptAgreement(null, "Report Download Failed",
							"Unrecognized response from server", e.getMessage());
				}
			});				
			status = new MultiStatus(BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR,
					"Unrecognized response from Bugzilla server " + repository.getUrl(), e);

			IStatus s = new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
					+ ":  ", e);
			((MultiStatus) status).add(s);
			s = new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "search failed", e);
			((MultiStatus) status).add(s);

		} catch (Exception e) {
			status = new MultiStatus(BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR,
					"An error occurred while querying Bugzilla Server " + repository.getUrl() + ".\n"
							+ "\nCheck network connection and repository configuration in " + TaskRepositoriesView.NAME
							+ ".", e);

			IStatus s = new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
					+ ":  ", e);
			((MultiStatus) status).add(s);
			s = new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "search failed", e);
			((MultiStatus) status).add(s);
		} finally {
			monitor.done();
			collector.done();
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaUiPlugin.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}

		// if we haven't collected any serach results and we suspect a bad
		// login, we assume it was a bad login
		if (numCollected == 0 && possibleBadLogin) {
			throw new LoginException(IBugzillaConstants.MESSAGE_LOGIN_FAILURE + " for repository: "
					+ repository.getUrl() + " username: " + repository.getUserName());
		}

		if (status == null)
			return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
		else
			return status;
	}

	/** Old code used by a unit test. */
	public static BugzillaSearchHit createHit(Pattern regularExpression, IProgressMonitor monitor, BufferedReader in,
			String serverUrl, int id) throws IOException {
		String line;
		String severity = null;
		String priority = null;
		String platform = null;
		String owner = null;
		String state = null;
		String result = null;
		for (int i = 0; i < 6; i++) {
			Matcher matcher;
			do {
				matcher = null;
				if (monitor.isCanceled()) {
					throw new OperationCanceledException("Search cancelled");
				}
				line = in.readLine();
				if (line == null)
					break;
				line = line.trim();
				matcher = regularExpression.matcher(line);
			} while (!matcher.find());
			if (null != matcher) {
				switch (i) {
				case 0:
					severity = matcher.group(1);
					break;
				case 1:
					priority = matcher.group(1);
					break;
				case 2:
					platform = matcher.group(1);
					break;
				case 3:
					owner = matcher.group(1);
					break;
				case 4:
					state = matcher.group(1);
					break;
				case 5:
					result = matcher.group(1);
					break;
				}
			}
		}

		// two more
		line = in.readLine();
		line = in.readLine();

		String description = "<activate to view description>";
		if (line != null) {
			description = line.substring(8);
		}
		if (description.startsWith(">")) {
			description = description.substring(1);
		}

		String query = "";
		try {
			String recentQuery = BugzillaUiPlugin.getMostRecentQuery();
			if (recentQuery != null)
				query = recentQuery;
		} catch (Exception e) {
			// ignore, for testing
		}

		BugzillaSearchHit hit = new BugzillaSearchHit(serverUrl, id, description, severity, priority, platform, state,
				result, owner, query);
		return hit;
	}

	public boolean isMaxReached() {
		return maxReached;
	}
}

// /** regular expression matching Bugzilla query results format used in
// Eclipse.org Bugzilla */
// protected static final Pattern re = Pattern.compile("<a
// href=\"show_bug.cgi\\?id=(\\d+)\">", Pattern.CASE_INSENSITIVE);
//
//	
// /** regular expression matching Bugzilla query results format used in
// v2.12 */
// protected static final Pattern reOld = Pattern.compile("<a
// href=\"show_bug.cgi\\?id=(\\d+)\">\\d+</a>\\s*<td
// class=severity><nobr>([^>]+)</nobr><td
// class=priority><nobr>([^>]+)</nobr><td
// class=platform><nobr>([^>]*)</nobr><td
// class=owner><nobr>([^>]*)</nobr><td class=status><nobr>([^>]*)</nobr><td
// class=resolution><nobr>([^>]*)</nobr><td class=summary>(.*)$",
// Pattern.CASE_INSENSITIVE);

// /**
// * Executes the query, parses the response, and adds hits to the search
// result collector.
// *
// * <p>
// * The output for a single match looks like this:
// * <pre>
// * <tr class="bz_enhancement bz_P5 ">
// *
// * <td>
// * <a href="show_bug.cgi?id=6747">6747</a>
// * </td>
// *
// * <td><nobr>enh</nobr>
// * </td>
// * <td><nobr>P5</nobr>
// * </td>
// * <td><nobr>All</nobr>
// * </td>
// * <td><nobr>Olivier_Thomann@oti.com</nobr>
// * </td>
// * <td><nobr>ASSI</nobr>
// * </td>
// * <td><nobr></nobr>
// * </td>
// * <td>Code Formatter exchange several blank lines w/ one
// * </td>
// *
// * </tr>
// * <pre>
// *
// * <p>Or in the older format:
// * <pre>
// * <A HREF="show_bug.cgi?id=8">8</A> <td
// class=severity><nobr>blo</nobr><td class=priority><nobr>P1</nobr><td
// class=platform><nobr>PC</nobr><td
// class=owner><nobr>cubranic@cs.ubc.ca</nobr><td
// class=status><nobr>CLOS</nobr><td class=resolution><nobr>DUPL</nobr><td
// class=summary>"Document root" missing when querying on files and
// revisions
// * </pre>
// * @param collector - The collector for the search results
// * @param startMatches - The number of matches to start with for the
// progress monitor
// * @param maxMatches - the maximum number of matches to return or -1 for
// unlimited
// */
// public IStatus search(IBugzillaSearchResultCollector collector, int
// startMatches, int maxMatches)
// throws LoginException {
// IProgressMonitor monitor = collector.getProgressMonitor();
// IStatus status = null;
// boolean possibleBadLogin = false;
// int numCollected = 0;
// BufferedReader in = null;
//
// try {
// monitor.beginTask(QUERYING_SERVER, IProgressMonitor.UNKNOWN);
// collector.aboutToStart(startMatches);
//
// URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(new
// URL(urlString));
// if (cntx == null || !(cntx instanceof HttpURLConnection)) {
// return null;
// }
//
// HttpURLConnection connect = (HttpURLConnection) cntx;
// connect.connect();
// int responseCode = connect.getResponseCode();
// if (responseCode != HttpURLConnection.HTTP_OK) {
// String msg;
// if (responseCode == -1 || responseCode ==
// HttpURLConnection.HTTP_FORBIDDEN)
// msg = repository.getUrl()
// + " does not seem to be a valid Bugzilla server. Check Bugzilla
// preferences.";
// else
// msg = "HTTP Error " + responseCode + " (" + connect.getResponseMessage()
// + ") while querying Bugzilla Server. Check Bugzilla preferences.";
//
// throw new BugzillaException(msg);
// }
//
// if (monitor.isCanceled()) {
// throw new OperationCanceledException("Search cancelled");
// }
//
// in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
// if (monitor.isCanceled()) {
// throw new OperationCanceledException("Search cancelled");
// }
//
// String line;
// while ((line = in.readLine()) != null) {
// if (maxMatches != -1 && numCollected >= maxMatches) {
// maxReached = true;
// break;
// }
//
// if (monitor.isCanceled()) {
// throw new OperationCanceledException("Search cancelled");
// }
//
// // create regular expressions that can be mathced to check if we
// // have
// // bad login information
// Pattern loginRe = Pattern.compile("<title>.*login.*</title>.*");
// Pattern invalidRe =
// Pattern.compile(".*<title>.*invalid.*password.*</title>.*");
// Pattern passwordRe =
// Pattern.compile(".*<title>.*password.*invalid.*</title>.*");
// Pattern emailRe = Pattern.compile(".*<title>.*check e-mail.*</title>.*");
// Pattern errorRe = Pattern.compile(".*<title>.*error.*</title>.*");
//
// String lowerLine = line.toLowerCase();
//
// // check if we have anything that suggests bad login info
// if (loginRe.matcher(lowerLine).find() ||
// invalidRe.matcher(lowerLine).find() ||
// passwordRe.matcher(lowerLine).find()
// || emailRe.matcher(lowerLine).find() ||
// errorRe.matcher(lowerLine).find())
// possibleBadLogin = true;
//
// Matcher matcher = reOld.matcher(line);
// if (matcher.find()) {
// int id = Integer.parseInt(matcher.group(1));
// String severity = matcher.group(2);
// String priority = matcher.group(3);
// String platform = matcher.group(4);
// String owner = matcher.group(5);
// String state = matcher.group(6);
// String result = matcher.group(7);
// String description = matcher.group(8);
// String query = BugzillaPlugin.getMostRecentQuery();
// if (query == null)
// query = "";
//
// String server = repository.getUrl();
//
// BugzillaSearchHit hit = new BugzillaSearchHit(server, id, description,
// severity, priority,
// platform, state, result, owner, query);
// collector.accept(hit);
// numCollected++;
//
// } else {
// matcher = re.matcher(line);
// if (matcher.find()) {
// Pattern regularExpression;
//						
// BugzillaServerVersion bugzillaServerVersion =
// IBugzillaConstants.BugzillaServerVersion.fromString(repository.getVersion());
// if (bugzillaServerVersion != null &&
// bugzillaServerVersion.compareTo(BugzillaServerVersion.SERVER_220) >= 0) {
// regularExpression = reValueBugzilla220;
// } else {
// regularExpression = reValue;
// }
//	
// int id = Integer.parseInt(matcher.group(1));
// BugzillaSearchHit hit = createHit(regularExpression, monitor, in,
// repository.getUrl(), id);
// collector.accept(hit);
// numCollected++;
// }
// }
//				
// // } else if (re.matches(line, match)) {
// // RegularExpression regularExpression;
// // if
// (repository.getVersion().equals(BugzillaServerVersion.SERVER_220.toString()))
// {
// // regularExpression = reValueBugzilla220;
// // } else {
// // regularExpression = reValue;
// // }
// //
// // int id = Integer.parseInt(match.getCapturedText(1));
// // BugzillaSearchHit hit = createHit(regularExpression, monitor, in,
// match, repository.getUrl()
// // .toExternalForm(), id);
// // collector.accept(hit);
// // numCollected++;
// // }
// if (monitor.isCanceled()) {
// throw new OperationCanceledException("Search cancelled");
// }
// }
// } catch (CoreException e) {
// status = new MultiStatus(IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
// "Core Exception occurred while querying Bugzilla Server " +
// repository.getUrl()
// + ".\n" + "\nClick Details for more information.", e);
// ((MultiStatus) status).add(e.getStatus());
//
// // write error to log
// BugzillaPlugin.log(status);
// } catch (OperationCanceledException e) {
// status = new Status(IStatus.CANCEL, IBugzillaConstants.PLUGIN_ID,
// IStatus.CANCEL, "", null);
// } catch (Exception e) {
// status = new MultiStatus(IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, "An
// error occurred while querying Bugzilla Server " + repository.getUrl() +
// ".\n"
// + "\nCheck network connection repository configuration in Task
// Repositories view.", e);
//
// IStatus s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID,
// IStatus.ERROR, e.getClass().toString()
// + ": ", e);
// ((MultiStatus) status).add(s);
// s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.OK,
// "search failed", e);
// ((MultiStatus) status).add(s);
//
// // write error to log
// //BugzillaPlugin.log(status);
//			
// 
// } finally {
// monitor.done();
// collector.done();
// try {
// if (in != null)
// in.close();
// } catch (IOException e) {
// BugzillaPlugin.log(new Status(IStatus.ERROR,
// IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
// "Problem closing the stream", e));
// }
// }
//
// // if we haven't collected any serach results and we suspect a bad
// // login, we assume it was a bad login
// if (numCollected == 0 && possibleBadLogin) {
// throw new LoginException(IBugzillaConstants.MESSAGE_LOGIN_FAILURE + " for
// repository: " + repository.getUrl() + " username: " +
// repository.getUserName());
// }
//
// if (status == null)
// return new Status(IStatus.OK, NewSearchUI.PLUGIN_ID, IStatus.OK, "",
// null);
// else
// return status;
// }
