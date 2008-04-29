/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.trac.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.WebHyperlink;

/**
 * Utility class for detecting Trac hyperlinks.
 * 
 * @author Steffen Pingel
 */
public class TracHyperlinkUtil {

	static Pattern ticketPattern = Pattern.compile("(ticket:|#)(\\d+)");

	static Pattern commentPattern = Pattern.compile("comment:ticket:(\\d+):(\\d+)");

	static Pattern reportPattern1 = Pattern.compile("report:(\\d+)");

	static Pattern reportPattern2 = Pattern.compile("\\{(\\d+)\\}");

	static Pattern changesetPattern1 = Pattern.compile("(r|changeset:)(\\d+)(/\\w+)?");

	static Pattern changesetPattern2 = Pattern.compile("\\[(\\d+)(/\\w+)?\\]");

	static Pattern revisionLogPattern1 = Pattern.compile("r(\\d+):(\\d+)");

	static Pattern revisionLogPattern2 = Pattern.compile("\\[(\\d+):(\\d+)\\]");

	static Pattern revisionLogPattern3 = Pattern.compile("log:(\\w+)?@(\\d+):(\\d+)");

	static Pattern diffPattern1 = Pattern.compile("diff:@(\\d+):(\\d+)");

	static Pattern diffPattern2 = Pattern.compile("diff:([\\w\\./-]+)(@(\\d+))?//([\\w\\./-]+)(@(\\d+))?");

	static Pattern wikiPattern1 = Pattern.compile("wiki:(\\w+)");

	static Pattern wikiPattern2 = Pattern.compile("[A-Z][a-z0-9]+[A-Z]\\w*");

	static Pattern milestonePattern = Pattern.compile("milestone:([\\w\\.]+)");

	static Pattern attachmentPattern = Pattern.compile("attachment:ticket:(\\d+):([\\w\\.]+)");

	static Pattern filesPattern = Pattern.compile("source:/*([\\w\\./\\-_]+)(@(\\d+)(#L(\\d+))?)?");

	/**
	 * Detects hyperlinks to Trac tickets.
	 */
	public static IHyperlink[] findTicketHyperlinks(TaskRepository repository, String text, int lineOffset,
			int regionOffset) {
		List<IHyperlink> links = null;
		Matcher m = ticketPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String id = m.group(2);
				if (links == null) {
					links = new ArrayList<IHyperlink>();
				}
				links.add(new TaskHyperlink(determineRegion(regionOffset, m), repository, id));
			}
		}
		return links == null ? null : links.toArray(new IHyperlink[0]);
	}

	/**
	 * Detects Trac hyperlinks.
	 * 
	 * <ul>
	 * <li>Ticket comments: comment:ticket:1:2
	 * <li>Reports: {1} or report:1
	 * <li>Changesets: r1, [1], changeset:1 or (restricted) [1/trunk], changeset:1/trunk
	 * <li>Revision log: r1:3, [1:3] or log:@1:3, log:trunk@1:3
	 * <li>Diffs: diff:@1:3, diff:tags/trac-0.9.2/wiki-default//tags/trac-0.9.3/wiki-default or
	 * diff:trunk/trac@3538//sandbox/vc-refactoring@3539
	 * <li>Wiki pages: CamelCase or wiki:CamelCase
	 * <li>Milestones: milestone:1.0
	 * <li>Attachment: attachment:ticket:944:attachment.1073.diff
	 * <li>Files: source:trunk/COPYING
	 * <li>A specific file revision: source:/trunk/COPYING@200
	 * <li>A particular line of a specific file revision: source:/trunk/COPYING@200#L25
	 * </ul>
	 * 
	 * @see http://trac.edgewall.org/wiki/TracLinks
	 */
	public static IHyperlink[] findTracHyperlinks(TaskRepository repository, String text, int lineOffset,
			int regionOffset) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();

		Matcher m = ticketPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String id = m.group(2);
				links.add(new TaskHyperlink(determineRegion(regionOffset, m), repository, id));
			}
		}

		m = commentPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String id = m.group(1);
				// String comment = m.group(2);
				links.add(new TaskHyperlink(determineRegion(regionOffset, m), repository, id));
			}
		}

		m = reportPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String id = m.group(1);
				links.add(new WebHyperlink(determineRegion(regionOffset, m), repository.getRepositoryUrl()
						+ ITracClient.REPORT_URL + id));
			}
		}

		m = reportPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String id = m.group(1);
				links.add(new WebHyperlink(determineRegion(regionOffset, m), repository.getRepositoryUrl()
						+ ITracClient.REPORT_URL + id));
			}
		}

		m = revisionLogPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String rev = m.group(1);
				String stopRev = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.REVISION_LOG_URL + "?rev=" + rev + "&stop_rev="
						+ stopRev;
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		m = revisionLogPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String rev = m.group(1);
				String stopRev = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.REVISION_LOG_URL + "?rev=" + rev + "&stop_rev="
						+ stopRev;
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		m = revisionLogPattern3.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String branch = m.group(1);
				String rev = m.group(2);
				String stopRev = m.group(3);
				String url = repository.getRepositoryUrl() + ITracClient.REVISION_LOG_URL;
				if (branch != null) {
					url += branch;
				}
				url += "?rev=" + rev + "&stop_rev=" + stopRev;
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		m = changesetPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String rev = m.group(2);
				String branch = m.group(3);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL + rev;
				if (branch != null) {
					url += branch;
				}
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		m = changesetPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String rev = m.group(1);
				String branch = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL + rev;
				if (branch != null) {
					url += branch;
				}
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		m = diffPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String old_rev = m.group(1);
				String new_rev = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL;
				url += "?new=" + new_rev + "&old=" + old_rev;
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		m = diffPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String old_path = m.group(1);
				String old_rev = m.group(3);
				String new_path = m.group(4);
				String new_rev = m.group(6);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL;
				try {
					url += "?new_path=" + URLEncoder.encode(new_path, "UTF-8");
					url += "&old_path=" + URLEncoder.encode(old_path, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					StatusHandler.log(new Status(IStatus.WARNING, TracUiPlugin.PLUGIN_ID, "Unexcpected exception", e));
					continue;
				}
				if (new_rev != null) {
					url += "&new=" + new_rev;
				}
				if (old_rev != null) {
					url += "&old=" + old_rev;
				}
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		m = wikiPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String page = m.group(1);
				links.add(new WebHyperlink(determineRegion(regionOffset, m), repository.getRepositoryUrl() + ITracClient.WIKI_URL
						+ page));
			}
		}

		m = wikiPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String page = m.group(0);
				links.add(new WebHyperlink(determineRegion(regionOffset, m), repository.getRepositoryUrl() + ITracClient.WIKI_URL
						+ page));
			}
		}

		m = milestonePattern.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String milestone = m.group(1);
				links.add(new WebHyperlink(determineRegion(regionOffset, m), repository.getRepositoryUrl()
						+ ITracClient.MILESTONE_URL + milestone));
			}
		}

		m = attachmentPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String id = m.group(1);
				// String attachment = m.group(2);
				links.add(new TaskHyperlink(determineRegion(regionOffset, m), repository, id));
			}
		}

		m = filesPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(lineOffset, m)) {
				String filename = m.group(1);
				String rev = m.group(3);
				String line = m.group(5);
				String url = repository.getRepositoryUrl() + ITracClient.BROWSER_URL + filename;
				if (rev != null) {
					url += "?rev=" + rev;
					if (line != null) {
						url += "#L" + line;
					}
				}
				links.add(new WebHyperlink(determineRegion(regionOffset, m), url));
			}
		}

		return links.isEmpty() ? null : links.toArray(new IHyperlink[0]);
	}

	private static boolean isInRegion(int lineOffset, Matcher m) {
		return (lineOffset >= m.start() && lineOffset <= m.end());
	}

	private static IRegion determineRegion(int regionOffset, Matcher m) {
		return new Region(regionOffset + m.start(), m.end() - m.start());
	}

}
