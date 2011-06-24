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

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * GitHub API request class.
 */
public class GitHubRequest {

	private String uri;

	private Map<String, String> params;

	private Type type;

	/**
	 * @return uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Get name value pairs for data map.
	 * 
	 * @param data
	 * @return name value pair array
	 */
	protected List<NameValuePair> getPairs(Map<String, String> data) {
		List<NameValuePair> pairs = new LinkedList<NameValuePair>();
		if (data != null && !data.isEmpty())
			for (Entry<String, String> entry : data.entrySet())
				pairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
		return pairs;
	}

	/**
	 * Generate full uri
	 * 
	 * @return uri
	 */
	public String generateUri() {
		if (uri.indexOf('?') != -1)
			return uri;
		String params = URLEncodedUtils.format(getPairs(getParams()), null);
		return uri + '?' + params;
	}

	/**
	 * @param uri
	 * @return this request
	 */
	public GitHubRequest setUri(StringBuilder uri) {
		return setUri(uri != null ? uri.toString() : null);
	}

	/**
	 * @param uri
	 * @return this request
	 */
	public GitHubRequest setUri(String uri) {
		this.uri = uri;
		return this;
	}

	/**
	 * @return params
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * @param params
	 * @return this request
	 */
	public GitHubRequest setParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	/**
	 * @return type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type
	 * @return this request
	 */
	public GitHubRequest setType(Type type) {
		this.type = type;
		return this;
	}
}
