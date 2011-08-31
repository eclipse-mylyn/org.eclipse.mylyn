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

import java.io.Serializable;
import java.util.Date;

import org.eclipse.egit.github.core.util.DateUtils;

/**
 * Gist revision class.
 */
public class GistRevision implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -7863453407918499259L;

	private Date committedAt;

	private GistChangeStatus changeStatus;

	private String url;

	private String version;

	private User user;

	/**
	 * @return committedAt
	 */
	public Date getCommittedAt() {
		return DateUtils.clone(committedAt);
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
