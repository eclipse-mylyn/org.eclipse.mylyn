/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.client;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

/**
 * Page link class to be used to determine the links to other pages of request
 * responses encoded in the current response. These will be present if the
 * result set size exceeds the per page limit.
 */
public class PageLinks {

	private static final String DELIM_LINKS = ","; //$NON-NLS-1$

	private static final String DELIM_LINK_PARAM = ";"; //$NON-NLS-1;$

	private String first;
	private String last;
	private String next;
	private String prev;

	/**
	 * Parse links from executed method
	 * 
	 * @param response
	 */
	public PageLinks(HttpResponse response) {
		Header[] linkHeaders = response
				.getHeaders(IGitHubConstants.HEADER_LINK);
		if (linkHeaders.length > 0) {
			String[] links = linkHeaders[0].getValue().split(DELIM_LINKS);
			for (String link : links) {
				String[] segments = link.split(DELIM_LINK_PARAM);
				if (segments.length < 2)
					continue;

				String linkPart = segments[0].trim();
				if (!linkPart.startsWith("<") || !linkPart.endsWith(">")) //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				linkPart = linkPart.substring(1, linkPart.length() - 1);

				for (int i = 1; i < segments.length; i++) {
					String[] rel = segments[i].trim().split("="); //$NON-NLS-1$
					if (rel.length < 2
							|| !IGitHubConstants.META_REL.equals(rel[0]))
						continue;

					String relValue = rel[1];
					if (relValue.startsWith("\"") && relValue.endsWith("\"")) //$NON-NLS-1$ //$NON-NLS-2$
						relValue = relValue.substring(1, relValue.length() - 1);

					if (IGitHubConstants.META_FIRST.equals(relValue))
						first = linkPart;
					else if (IGitHubConstants.META_LAST.equals(relValue))
						last = linkPart;
					else if (IGitHubConstants.META_NEXT.equals(relValue))
						next = linkPart;
					else if (IGitHubConstants.META_PREV.equals(relValue))
						prev = linkPart;
				}
			}
		} else {
			Header[] nextHeaders = response
					.getHeaders(IGitHubConstants.HEADER_NEXT);
			if (nextHeaders.length > 0)
				next = nextHeaders[0].getValue();

			Header[] lastHeaders = response
					.getHeaders(IGitHubConstants.HEADER_LAST);
			if (lastHeaders.length > 0)
				last = lastHeaders[0].getValue();
		}
	}

	/**
	 * @return first
	 */
	public String getFirst() {
		return this.first;
	}

	/**
	 * @return last
	 */
	public String getLast() {
		return this.last;
	}

	/**
	 * @return next
	 */
	public String getNext() {
		return this.next;
	}

	/**
	 * @return prev
	 */
	public String getPrev() {
		return this.prev;
	}
}
