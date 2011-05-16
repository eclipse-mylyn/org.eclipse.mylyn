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

import org.apache.http.HttpResponse;

/**
 * GitHub API response class.
 */
public class GitHubResponse {

	private PageLinks links;

	private Object body;

	/**
	 * Create response
	 * 
	 * @param method
	 * @param body
	 */
	public GitHubResponse(HttpResponse response, Object body) {
		links = new PageLinks(response);
		this.body = body;
	}

	/**
	 * Get link uri to first page
	 * 
	 * @return possibly null uri
	 */
	public String getFirst() {
		return this.links.getFirst();
	}

	/**
	 * Get link uri to previous page
	 * 
	 * @return possibly null uri
	 */
	public String getPrevious() {
		return this.links.getPrev();
	}

	/**
	 * Get link uri to next page
	 * 
	 * @return possibly null uri
	 */
	public String getNext() {
		return this.links.getNext();
	}

	/**
	 * Get link uri to last page
	 * 
	 * @return possibly null uri
	 */
	public String getLast() {
		return this.links.getLast();
	}

	/**
	 * Parsed response body
	 * 
	 * @return body
	 */
	public Object getBody() {
		return this.body;
	}

}
