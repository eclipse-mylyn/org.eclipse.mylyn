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

import java.util.Date;

/**
 * GitHub issue comment class.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Comment {

	private Date createdAt;

	private Date updatedAt;

	private String body;

	private String url;

	private User user;

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		// see EI_EXPOSE_REP at
		// http://findbugs.sourceforge.net/bugDescriptions.html
		return new Date(this.createdAt.getTime());
	}

	/**
	 * @return updatedAt
	 */
	public Date getUpdatedAt() {
		// see EI_EXPOSE_REP at
		// http://findbugs.sourceforge.net/bugDescriptions.html
		return new Date(this.updatedAt.getTime());
	}

	/**
	 * @return body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

}
