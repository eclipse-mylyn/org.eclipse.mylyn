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
public class GitHubIssueComment {

	private String gravatar_id;
	private Date created_at;
	private String body;
	private Date updated_at;
	private String id;
	private String user;

	/**
	 * Get gravatar id
	 *
	 * @return gravatar id
	 */
	public String getGravatarId() {
		return this.gravatar_id;
	}

	/**
	 * Get created at date
	 *
	 * @return created date
	 */
	public Date getCreatedAt() {
		return this.created_at;
	}

	/**
	 * Get body
	 *
	 * @return body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * Get updated at date
	 *
	 * @return date
	 */
	public Date getUpdatedAt() {
		return this.updated_at;
	}

	/**
	 * Get id
	 *
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get user
	 *
	 * @return user
	 */
	public String getUser() {
		return this.user;
	}

}
