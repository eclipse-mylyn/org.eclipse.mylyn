/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class JenkinsUrl {

	private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

	public static JenkinsUrl create(String base) {
		JenkinsUrl url = new JenkinsUrl();
		url.base = base;
		return url;
	}

	String base;

	int depth = 1;

	List<String> exclude;

	String include;

	String key;

	List<String> values;

	String tree;

	private JenkinsUrl() {
	}

	public JenkinsUrl depth(int depth) {
		this.depth = depth;
		return this;
	}

	public JenkinsUrl exclude(String exclude) {
		if (this.exclude == null) {
			this.exclude = new ArrayList<>();
		}
		this.exclude.add(exclude);
		return this;
	}

	public JenkinsUrl include(String include) {
		this.include = include;
		return this;
	}

	public JenkinsUrl match(String key, List<String> values) {
		this.key = key;
		this.values = values;
		return this;
	}

	public JenkinsUrl tree(String tree) {
		this.tree = tree;
		return this;
	}

	public String toUrl() throws UnsupportedEncodingException {
		// wrap everything in "hudson" element to handle case of multiple matches
		StringBuilder sb = new StringBuilder(base);
		if (!base.endsWith("/")) { //$NON-NLS-1$
			sb.append("/"); //$NON-NLS-1$
		}
		sb.append("api/xml?wrapper=hudson&depth="); //$NON-NLS-1$
		sb.append(depth);
		if (include != null) {
			sb.append("&xpath="); //$NON-NLS-1$
			sb.append(include);
			if (key != null && values != null) {
				sb.append(getFilter());
			}
		}
		if (exclude != null) {
			for (String value : exclude) {
				sb.append("&exclude="); //$NON-NLS-1$
				sb.append(value);
			}
		}
		if (tree != null) {
			sb.append("&tree="); //$NON-NLS-1$
			sb.append(URLEncoder.encode(tree, "UTF-8")); //$NON-NLS-1$
		}
		return sb.toString();
	}

	protected String getFilter() throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("["); //$NON-NLS-1$
		boolean appendSeparator = false;
		for (String value : values) {
			if (appendSeparator) {
				sb.append(" or "); //$NON-NLS-1$
			} else {
				appendSeparator = true;
			}
			sb.append(key);
			sb.append("="); //$NON-NLS-1$
			quote(sb, value);
		}
		sb.append("]"); //$NON-NLS-1$
		return URLEncoder.encode(sb.toString(), ENCODING);
	}

	private void quote(StringBuilder sb, String value) {
		char quote = '\'';
		if (value.contains("'")) { //$NON-NLS-1$
			if (value.contains("\"")) { //$NON-NLS-1$
				throw new IllegalArgumentException(
						"Cannot query for a job which contains both a single and double quote in its name"); //$NON-NLS-1$
			}
			quote = '"';
		}
		sb.append(quote);
		sb.append(value);
		sb.append(quote);
	}
}
