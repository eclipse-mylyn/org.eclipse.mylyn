/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.tests.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * A builder class for constructing {@link TaskRepository task repository} URLs.
 * 
 * @author David Green
 * @since 3.5
 */
public class UrlBuilder {
	private final StringBuilder buf = new StringBuilder(512);

	private UrlBuilder() {
	}

	public static UrlBuilder build(TaskRepository repository) {
		UrlBuilder builder = new UrlBuilder();
		String url = repository.getRepositoryUrl();
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return builder.append(url);
	}

	public UrlBuilder append(String urlSegment) {
		buf.append(urlSegment);
		return this;
	}

	public UrlBuilder parameter(String name, Object value) {
		return parameter(name, value == null ? null : value.toString());
	}

	public UrlBuilder parameter(String name, String value) {
		int indexOfQ = buf.indexOf("?");
		if (indexOfQ == -1) {
			buf.append("?");
		} else {
			buf.append("&");
		}
		buf.append(name);
		buf.append('=');
		if (value != null) {
			buf.append(encode(value));
		}
		return this;
	}

	private String encode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return buf.toString();
	}

}
