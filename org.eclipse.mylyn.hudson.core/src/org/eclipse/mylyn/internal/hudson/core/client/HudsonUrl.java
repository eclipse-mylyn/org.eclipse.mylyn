/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.core.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class HudsonUrl {

	public static HudsonUrl create(String base) {
		HudsonUrl url = new HudsonUrl();
		url.base = base;
		return url;
	}

	String base;

	int depth = 1;

	List<String> exclude;

	String include;

	String key;

	List<String> values;

	private HudsonUrl() {
	}

	public HudsonUrl depth(int depth) {
		this.depth = depth;
		return this;
	}

	public HudsonUrl exclude(String exclude) {
		if (this.exclude == null) {
			this.exclude = new ArrayList<String>();
		}
		this.exclude.add(exclude);
		return this;
	}

	public HudsonUrl include(String include) {
		this.include = include;
		return this;
	}

	public HudsonUrl match(String key, List<String> values) {
		this.key = key;
		this.values = values;
		return this;
	}

	public String toUrl() throws UnsupportedEncodingException {
		// wrap everything in "hudson" element to handle case of multiple matches
		StringBuilder sb = new StringBuilder(base + "?wrapper=hudson&depth=");
		sb.append(depth);
		if (include != null) {
			sb.append("&xpath=");
			sb.append(include);
			if (key != null && values != null) {
				sb.append(getFilter());
			}
		}
		if (exclude != null) {
			for (String value : exclude) {
				sb.append("&exclude=");
				sb.append(value);
			}
		}
		return sb.toString();
	}

	protected String getFilter() throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean appendSeparator = false;
		for (String value : values) {
			if (appendSeparator) {
				sb.append(" or ");
			} else {
				appendSeparator = true;
			}
			sb.append(key);
			sb.append("=");
			sb.append("'");
			sb.append(value);
			sb.append("'");
		}
		sb.append("]");
		return URLEncoder.encode(sb.toString(), "UTF-8");
	}
}
