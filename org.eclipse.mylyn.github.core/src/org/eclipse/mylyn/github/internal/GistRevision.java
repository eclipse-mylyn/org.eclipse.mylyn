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
 * Gist revision class.
 */
public class GistRevision {

	private Date committedAt;

	private GistChangeStatus changeStatus;

	private String url;

	private String version;

	private User user;

	/**
	 * @return committedAt
	 */
	public Date getCommittedAt() {
		return this.committedAt != null ? new Date(this.committedAt.getTime())
				: null;
	}

	/**
	 * @return changeStatus
	 */
	public GistChangeStatus getChangeStatus() {
		return this.changeStatus;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @return version
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

}
