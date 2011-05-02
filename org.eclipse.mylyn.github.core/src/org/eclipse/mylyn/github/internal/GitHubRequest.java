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
package org.eclipse.mylyn.github.internal;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * GitHub API request class.
 */
public class GitHubRequest {

	private String uri;

	private Map<String, String> params;

	private Type type;

	private int page = 1;

	/**
	 * @return uri
	 */
	public String getUri() {
		return this.uri;
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
		return this.params;
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
		return this.type;
	}

	/**
	 * @param type
	 * @return this request
	 */
	public GitHubRequest setType(Type type) {
		this.type = type;
		return this;
	}

	/**
	 * @return page
	 */
	public int getPage() {
		return this.page;
	}

	/**
	 * @param page
	 * @return this request
	 */
	public GitHubRequest setPage(int page) {
		this.page = page;
		return this;
	}

}
