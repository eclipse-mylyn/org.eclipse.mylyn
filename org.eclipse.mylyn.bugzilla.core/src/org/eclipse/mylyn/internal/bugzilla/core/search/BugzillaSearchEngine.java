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
package org.eclipse.mylar.internal.bugzilla.core.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.search.ui.NewSearchUI;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

/**
 * Queries the Bugzilla server for the list of bugs matching search criteria.
 * 
 * @author Mik Kersten (hardening of initial prototype)
 */
public class BugzillaSearchEngine {

	protected static final String QUERYING_SERVER = "Querying Bugzilla Server...";

	/** regular expression matching Bugzilla query results format used in Eclipse.org Bugzilla */
	protected static final RegularExpression re = new RegularExpression("<a href=\"show_bug.cgi\\?id=(\\d+)\">", "i");

	/** regular expression matching values of query matches' attributes in Eclipse.org Bugzilla */
	public static final RegularExpression reValue = new RegularExpression("<td><nobr>([^<]*)</nobr>");

	public static final RegularExpression reValueBugzilla220 = new RegularExpression(
			"<td style=\"white-space: nowrap\">([^<]*)");

	/** regular expression matching Bugzilla query results format used in v2.12 */
	protected static final RegularExpression reOld = new RegularExpression(
			"<a href=\"show_bug.cgi\\?id=(\\d+)\">\\d+</a>\\s*<td class=severity><nobr>([^>]+)</nobr><td class=priority><nobr>([^>]+)</nobr><td class=platform><nobr>([^>]*)</nobr><td class=owner><nobr>([^>]*)</nobr><td class=status><nobr>([^>]*)</nobr><td class=resolution><nobr>([^>]*)</nobr><td class=summary>(.*)$",
			"i");

	private String urlString;

	private TaskRepository repository;

	private boolean maxReached = false;

	public BugzillaSearchEngine(TaskRepository repository, String queryUrl) {
		urlString = queryUrl;
		this.repository = repository;

		if (repository.hasCredentials()) {
			try {
				urlString += "&GoAheadAndLogIn=1&Bugzilla_login="
						+ URLEncoder.encode(repository.getUserName(), BugzillaPlugin.ENCODING_UTF_8)
						+ "&Bugzilla_password="
						+ URLEncoder.encode(repository.getPassword(), BugzillaPlugin.ENCODING_UTF_8);
			} catch (UnsupportedEncodingException e) {
				/*
				 * Do nothing. Every implementation of the Java platform is required
				 * to support the standard charset "UTF-8"
				 */
			}
		}
		// use the username and password if we have it to log into bugzilla
		//		if(BugzillaPreferencePage.getUserName() != null && !BugzillaPreferencePage.getUserName().equals("") && BugzillaPreferencePage.getPassword() != null && !BugzillaPreferencePage.getPassword().equals(""))
		//		{
		//			try {
		//				url += "&GoAheadAndLogIn=1&Bugzilla_login=" + URLEncoder.encode(BugzillaPreferencePage.getUserName(), "UTF-8") + "&Bugzilla_password=" + URLEncoder.encode(BugzillaPreferencePage.getPassword(), "UTF-8");
		//			} catch (UnsupportedEncodingException e) {
		//				/*
		//				 * Do nothing. Every implementation of the Java platform is required
		//				 * to support the standard charset "UTF-8"
		//				 */
		//			}
		//		}
	}

	/**
	 * Wrapper for search
	 * @param collector - The collector for the results to go into
	 */
	public IStatus search(IBugzillaSearchResultCollector collector) throws LoginException {
		return this.search(collector, 0, -1);
	}

	/**
	 * Wrapper for search
	 * @param collector - The collector for the results to go into
	 * @param startMatches - The number of matches to start with for the progress monitor
	 */
	public IStatus search(IBugzillaSearchResultCollector collector, int startMatches) throws LoginException {
		return this.search(collector, startMatches, BugzillaPlugin.getDefault().getMaxResults());
	}

	/**
	 * Executes the query, parses the response, and adds hits to the search result collector.
	 * 
	 * <p>
	 * The output for a single match looks like this:
	 * <pre>
	 *  <tr class="bz_enhancement bz_P5 ">
	 *
	 *    <td>
	 *      <a href="show_bug.cgi?id=6747">6747</a>
	 *    </td>
	 *
	 *    <td><nobr>enh</nobr>
	 *    </td>
	 *    <td><nobr>P5</nobr>
	 *    </td>
	 *    <td><nobr>All</nobr>
	 *    </td>
	 *    <td><nobr>Olivier_Thomann@oti.com</nobr>
	 *    </td>
	 *    <td><nobr>ASSI</nobr>
	 *    </td>
	 *    <td><nobr></nobr>
	 *    </td>
	 *    <td>Code Formatter exchange several blank lines  w/ one
	 *    </td>
	 *
	 *  </tr>
	 * <pre>
	 * 
	 * <p>Or in the older format:
	 * <pre>
	 * <A HREF="show_bug.cgi?id=8">8</A> <td class=severity><nobr>blo</nobr><td class=priority><nobr>P1</nobr><td class=platform><nobr>PC</nobr><td class=owner><nobr>cubranic@cs.ubc.ca</nobr><td class=status><nobr>CLOS</nobr><td class=resolution><nobr>DUPL</nobr><td class=summary>"Document root" missing when querying on files and revisions
	 * </pre>
	 * @param collector - The collector for the search results
	 * @param startMatches - The number of matches to start with for the progress monitor
	 * @param maxMatches - the maximum number of matches to return or -1 for unlimited
	 */
	public IStatus search(IBugzillaSearchResultCollector collector, int startMatches, int maxMatches)
			throws LoginException {
		IProgressMonitor monitor = collector.getProgressMonitor();
		IStatus status = null;
		boolean possibleBadLogin = false;
		int numCollected = 0;
		BufferedReader in = null;

		try {
			monitor.beginTask(QUERYING_SERVER, IProgressMonitor.UNKNOWN);
			collector.aboutToStart(startMatches);

			URLConnection cntx = BugzillaPlugin.getDefault().getUrlConnection(new URL(urlString));
			if (cntx == null || !(cntx instanceof HttpURLConnection)) {
				return null;
			}

			HttpURLConnection connect = (HttpURLConnection) cntx;
			connect.connect();
			int responseCode = connect.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				String msg;
				if (responseCode == -1 || responseCode == HttpURLConnection.HTTP_FORBIDDEN)
					msg = repository.getUrl().toExternalForm()
							+ " does not seem to be a valid Bugzilla server.  Check Bugzilla preferences.";
				else
					msg = "HTTP Error " + responseCode + " (" + connect.getResponseMessage()
							+ ") while querying Bugzilla Server.  Check Bugzilla preferences.";

				throw new BugzillaException(msg);
			}

			if (monitor.isCanceled()) {
				throw new OperationCanceledException("Search cancelled");
			}

			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			if (monitor.isCanceled()) {
				throw new OperationCanceledException("Search cancelled");
			}

			Match match = new Match();
			String line;
			while ((line = in.readLine()) != null) {
				if (maxMatches != -1 && numCollected >= maxMatches) {
					maxReached = true;
					break;
				}

				if (monitor.isCanceled()) {
					throw new OperationCanceledException("Search cancelled");
				}

				// create regular expressions that can be mathced to check if we
				// have
				// bad login information
				RegularExpression loginRe = new RegularExpression("<title>.*login.*</title>.*");
				RegularExpression invalidRe = new RegularExpression(".*<title>.*invalid.*password.*</title>.*");
				RegularExpression passwordRe = new RegularExpression(".*<title>.*password.*invalid.*</title>.*");
				RegularExpression emailRe = new RegularExpression(".*<title>.*check e-mail.*</title>.*");
				RegularExpression errorRe = new RegularExpression(".*<title>.*error.*</title>.*");

				String lowerLine = line.toLowerCase();

				// check if we have anything that suggests bad login info
				if (loginRe.matches(lowerLine) || invalidRe.matches(lowerLine) || passwordRe.matches(lowerLine)
						|| emailRe.matches(lowerLine) || errorRe.matches(lowerLine))
					possibleBadLogin = true;

				if (reOld.matches(line, match)) {
					int id = Integer.parseInt(match.getCapturedText(1));
					String severity = match.getCapturedText(2);
					String priority = match.getCapturedText(3);
					String platform = match.getCapturedText(4);
					String owner = match.getCapturedText(5);
					String state = match.getCapturedText(6);
					String result = match.getCapturedText(7);
					String description = match.getCapturedText(8);
					String query = BugzillaPlugin.getMostRecentQuery();
					if (query == null)
						query = "";

					// String server =
					// BugzillaPlugin.getDefault().getServerName();
					String server = repository.getUrl().toExternalForm();

					BugzillaSearchHit hit = new BugzillaSearchHit(server, id, description, severity, priority,
							platform, state, result, owner, query);
					collector.accept(hit);
					numCollected++;

				} else if (re.matches(line, match)) {
					RegularExpression regularExpression;
					if (BugzillaPlugin.getDefault().isServerCompatability220()) {
						regularExpression = reValueBugzilla220;
					} else {
						regularExpression = reValue;
					}

					int id = Integer.parseInt(match.getCapturedText(1));
					BugzillaSearchHit hit = createHit(regularExpression, monitor, in, match, repository.getUrl()
							.toExternalForm(), id);
					collector.accept(hit);
					numCollected++;
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException("Search cancelled");
				}
			}
		} catch (CoreException e) {
			status = new MultiStatus(IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
					"Core Exception occurred while querying Bugzilla Server " + repository.getUrl().toExternalForm()
							+ ".\n" + "\nClick Details for more information.", e);
			((MultiStatus) status).add(e.getStatus());

			// write error to log
			BugzillaPlugin.log(status);
		} catch (OperationCanceledException e) {
			status = new Status(IStatus.CANCEL, IBugzillaConstants.PLUGIN_ID, IStatus.CANCEL, "", null);
		} catch (Exception e) {
			status = new MultiStatus(IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
					+ " occurred while querying Bugzilla Server " + repository.getUrl().toExternalForm() + ".\n"
					+ "\nClick Details or see log for more information.", e);

			IStatus s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString()
					+ ":  ", e);
			((MultiStatus) status).add(s);
			s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.OK, "search failed", e);
			((MultiStatus) status).add(s);

			// write error to log
			BugzillaPlugin.log(status);

		} finally {
			monitor.done();
			collector.done();
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}

		// if we haven't collected any serach results and we suspect a bad
		// login, we assume it was a bad login
		if (numCollected == 0 && possibleBadLogin)
			throw new LoginException(IBugzillaConstants.MESSAGE_LOGIN_FAILURE);

		if (status == null)
			return new Status(IStatus.OK, NewSearchUI.PLUGIN_ID, IStatus.OK, "", null);
		else
			return status;
	}

	public static BugzillaSearchHit createHit(RegularExpression regularExpression, IProgressMonitor monitor,
			BufferedReader in, Match match, String serverUrl, int id) throws IOException {
		String line;
		String severity = null;
		String priority = null;
		String platform = null;
		String owner = null;
		String state = null;
		String result = null;
		for (int i = 0; i < 6; i++) {
			do {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException("Search cancelled");
				}
				line = in.readLine();
				if (line == null)
					break;
				line = line.trim();
			} while (!regularExpression.matches(line, match));
			switch (i) {
			case 0:
				severity = match.getCapturedText(1);
				break;
			case 1:
				priority = match.getCapturedText(1);
				break;
			case 2:
				platform = match.getCapturedText(1);
				break;
			case 3:
				owner = match.getCapturedText(1);
				break;
			case 4:
				state = match.getCapturedText(1);
				break;
			case 5:
				result = match.getCapturedText(1);
				break;
			}
		}

		// two more
		line = in.readLine();
		line = in.readLine();

		String description = "<activate to view description>";
		if (line != null)
			description = line.substring(8);

		String query = "";
		// String server = "<unknown server>";
		try {
			String recentQuery = BugzillaPlugin.getMostRecentQuery();
			if (recentQuery != null)
				query = recentQuery;
			// server = BugzillaPlugin.getDefault().getServerName();
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
