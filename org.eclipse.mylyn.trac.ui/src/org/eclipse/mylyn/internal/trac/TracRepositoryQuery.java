/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryQuery extends AbstractRepositoryQuery {

	public TracRepositoryQuery(String repositoryUrl, String queryUrl, String description, TaskList taskList) {
		super(description, taskList);

		assert queryUrl.startsWith(repositoryUrl + ITracClient.QUERY_URL);

		setRepositoryUrl(repositoryUrl);
		setUrl(queryUrl);
	}

	@Override
	public String getRepositoryKind() {
		return TracUiPlugin.REPOSITORY_KIND;
	}

	public String getQueryParameter() {
		String url = getUrl();
		int i = url.indexOf(ITracClient.QUERY_URL);
		if (i == -1) {
			return null;
		}
		return url.substring(i + ITracClient.QUERY_URL.length());
	}

	/**
	 * Creates a <code>TracSearch</code> object from this query.
	 */
	public TracSearch getTracSearch() {
		TracSearch list = new TracSearch();
		String url = getQueryParameter();
		if (url == null) {
			return list;
		}

		StringTokenizer t = new StringTokenizer(url, "&");
		while (t.hasMoreTokens()) {
			String token = t.nextToken();
			int i = token.indexOf("=");
			if (i != -1) {
				try {
					String key = URLDecoder.decode(token.substring(0, i), ITracClient.CHARSET);
					String value = URLDecoder.decode(token.substring(i + 1), ITracClient.CHARSET);

					if ("order".equals(key)) {
						list.setOrderBy(value);
					} else if ("desc".equals(key)) {
						list.setAscending(!"1".equals(value));
					} else if ("group".equals(key) || "groupdesc".equals(key) || "verbose".equals(key)) {
						// ignore these parameters
					} else {
						list.addFilter(key, value);
					}
				} catch (UnsupportedEncodingException e) {
					MylarStatusHandler.log(e, "Unexpected exception while decoding URL");
				}
			}
		}

		return list;
	}

}
