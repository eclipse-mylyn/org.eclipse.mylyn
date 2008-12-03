/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green - fix for bug 244017
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
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;

/**
 * Utility class for detecting Trac hyperlinks.
 * 
 * @author Steffen Pingel
 */
public class TracHyperlinkUtil {

	static Pattern ticketPattern = createPattern("(ticket:|#)(\\d+)"); //$NON-NLS-1$

	static Pattern commentPattern = createPattern("comment:ticket:(\\d+):(\\d+)"); //$NON-NLS-1$

	static Pattern reportPattern1 = createPattern("report:(\\d+)"); //$NON-NLS-1$

	static Pattern reportPattern2 = createPattern("\\{(\\d+)\\}"); //$NON-NLS-1$

	static Pattern changesetPattern1 = createPattern("(r|changeset:)(\\d+)(/\\w+)?"); //$NON-NLS-1$

	static Pattern changesetPattern2 = createPattern("\\[(\\d+)(/\\w+)?\\]"); //$NON-NLS-1$

	static Pattern revisionLogPattern1 = createPattern("r(\\d+):(\\d+)"); //$NON-NLS-1$

	static Pattern revisionLogPattern2 = createPattern("\\[(\\d+):(\\d+)\\]"); //$NON-NLS-1$

	static Pattern revisionLogPattern3 = createPattern("log:(\\w+)?@(\\d+):(\\d+)"); //$NON-NLS-1$

	static Pattern diffPattern1 = createPattern("diff:@(\\d+):(\\d+)"); //$NON-NLS-1$

	static Pattern diffPattern2 = createPattern("diff:([\\w\\./-]+)(@(\\d+))?//([\\w\\./-]+)(@(\\d+))?"); //$NON-NLS-1$

	static Pattern wikiPattern1 = createPattern("wiki:(\\w+)"); //$NON-NLS-1$

	static Pattern wikiPattern2 = Pattern.compile("(?<![!.a-z])[A-Z][a-z0-9]+[A-Z]\\w*"); //$NON-NLS-1$

	static Pattern milestonePattern = createPattern("milestone:([\\w\\.]+)"); //$NON-NLS-1$

	static Pattern attachmentPattern = createPattern("attachment:ticket:(\\d+):([\\w\\.]+)"); //$NON-NLS-1$

	static Pattern filesPattern = createPattern("source:/*([\\w\\./\\-_]+)(@(\\d+)(#L(\\d+))?)?"); //$NON-NLS-1$

	private static Pattern createPattern(String regexp) {
		// hyperlink patterns prefixed with "!" are not links
		return Pattern.compile("(?<!!)" + regexp); //$NON-NLS-1$
	}

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
	public static List<IHyperlink> findTracHyperlinks(TaskRepository repository, String text, int offsetInText,
			int textOffset) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();

		Matcher m = ticketPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String id = m.group(2);
				links.add(new TaskHyperlink(determineRegion(textOffset, m), repository, id));
			}
		}

		m = commentPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String id = m.group(1);
				// String comment = m.group(2);
				links.add(new TaskHyperlink(determineRegion(textOffset, m), repository, id));
			}
		}

		m = reportPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String id = m.group(1);
				links.add(new WebHyperlink(determineRegion(textOffset, m), repository.getRepositoryUrl()
						+ ITracClient.REPORT_URL + id));
			}
		}

		m = reportPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String id = m.group(1);
				links.add(new WebHyperlink(determineRegion(textOffset, m), repository.getRepositoryUrl()
						+ ITracClient.REPORT_URL + id));
			}
		}

		m = revisionLogPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String rev = m.group(1);
				String stopRev = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.REVISION_LOG_URL + "?rev=" + rev //$NON-NLS-1$
						+ "&stop_rev=" + stopRev; //$NON-NLS-1$
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		m = revisionLogPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String rev = m.group(1);
				String stopRev = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.REVISION_LOG_URL + "?rev=" + rev //$NON-NLS-1$
						+ "&stop_rev=" + stopRev; //$NON-NLS-1$
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		m = revisionLogPattern3.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String branch = m.group(1);
				String rev = m.group(2);
				String stopRev = m.group(3);
				String url = repository.getRepositoryUrl() + ITracClient.REVISION_LOG_URL;
				if (branch != null) {
					url += branch;
				}
				url += "?rev=" + rev + "&stop_rev=" + stopRev; //$NON-NLS-1$ //$NON-NLS-2$
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		m = changesetPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String rev = m.group(2);
				String branch = m.group(3);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL + rev;
				if (branch != null) {
					url += branch;
				}
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		m = changesetPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String rev = m.group(1);
				String branch = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL + rev;
				if (branch != null) {
					url += branch;
				}
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		m = diffPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String old_rev = m.group(1);
				String new_rev = m.group(2);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL;
				url += "?new=" + new_rev + "&old=" + old_rev; //$NON-NLS-1$ //$NON-NLS-2$
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		m = diffPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String old_path = m.group(1);
				String old_rev = m.group(3);
				String new_path = m.group(4);
				String new_rev = m.group(6);
				String url = repository.getRepositoryUrl() + ITracClient.CHANGESET_URL;
				try {
					url += "?new_path=" + URLEncoder.encode(new_path, "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
					url += "&old_path=" + URLEncoder.encode(old_path, "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (UnsupportedEncodingException e) {
					StatusHandler.log(new Status(IStatus.WARNING, TracUiPlugin.ID_PLUGIN, "Unexcpected exception", e)); //$NON-NLS-1$
					continue;
				}
				if (new_rev != null) {
					url += "&new=" + new_rev; //$NON-NLS-1$
				}
				if (old_rev != null) {
					url += "&old=" + old_rev; //$NON-NLS-1$
				}
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		m = wikiPattern1.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String page = m.group(1);
				links.add(new WebHyperlink(determineRegion(textOffset, m), repository.getRepositoryUrl()
						+ ITracClient.WIKI_URL + page));
			}
		}

		m = wikiPattern2.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String page = m.group(0);
				links.add(new WebHyperlink(determineRegion(textOffset, m), repository.getRepositoryUrl()
						+ ITracClient.WIKI_URL + page));
			}
		}

		m = milestonePattern.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String milestone = m.group(1);
				links.add(new WebHyperlink(determineRegion(textOffset, m), repository.getRepositoryUrl()
						+ ITracClient.MILESTONE_URL + milestone));
			}
		}

		m = attachmentPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String id = m.group(1);
				// String attachment = m.group(2);
				links.add(new TaskHyperlink(determineRegion(textOffset, m), repository, id));
			}
		}

		m = filesPattern.matcher(text);
		while (m.find()) {
			if (isInRegion(offsetInText, m)) {
				String filename = m.group(1);
				String rev = m.group(3);
				String line = m.group(5);
				String url = repository.getRepositoryUrl() + ITracClient.BROWSER_URL + filename;
				if (rev != null) {
					url += "?rev=" + rev; //$NON-NLS-1$
					if (line != null) {
						url += "#L" + line; //$NON-NLS-1$
					}
				}
				links.add(new WebHyperlink(determineRegion(textOffset, m), url));
			}
		}

		return links;
	}

	private static boolean isInRegion(int offsetInText, Matcher m) {
		return (offsetInText == -1) || (offsetInText >= m.start() && offsetInText <= m.end());
	}

	private static IRegion determineRegion(int textOffset, Matcher m) {
		return new Region(textOffset + m.start(), m.end() - m.start());
	}

}
