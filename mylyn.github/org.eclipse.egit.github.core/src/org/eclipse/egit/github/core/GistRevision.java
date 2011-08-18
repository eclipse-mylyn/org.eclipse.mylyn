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
package org.eclipse.egit.github.core;

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
		return committedAt != null ? new Date(committedAt.getTime()) : null;
	}

	/**
	 * @return changeStatus
	 */
	public GistChangeStatus getChangeStatus() {
		return changeStatus;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}
}
