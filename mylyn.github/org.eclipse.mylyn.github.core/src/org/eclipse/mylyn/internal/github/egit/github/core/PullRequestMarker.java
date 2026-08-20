/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.egit.github.core;

import java.io.Serializable;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

/**
 * Pull request marker model class.
 */
public class PullRequestMarker implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 5052026861072656918L;

	private GHRepository repo;

	private String label;

	private String ref;

	private String sha;

	private GHUser user;

	/**
	 * @return repo
	 */
	public GHRepository getRepo() {
		return repo;
	}

	/**
	 * @param repo
	 * @return this marker
	 */
	public PullRequestMarker setRepo(GHRepository repo) {
		this.repo = repo;
		return this;
	}

	/**
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 * @return this marker
	 */
	public PullRequestMarker setLabel(String label) {
		this.label = label;
		return this;
	}

	/**
	 * @return ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @param ref
	 * @return this marker
	 */
	public PullRequestMarker setRef(String ref) {
		this.ref = ref;
		return this;
	}

	/**
	 * @return sha
	 */
	public String getSha() {
		return sha;
	}

	/**
	 * @param sha
	 * @return this marker
	 */
	public PullRequestMarker setSha(String sha) {
		this.sha = sha;
		return this;
	}

	/**
	 * @return user
	 */
	public GHUser getUser() {
		return user;
	}

	/**
	 * @param user
	 * @return this marker
	 */
	public PullRequestMarker setUser(GHUser user) {
		this.user = user;
		return this;
	}
}
