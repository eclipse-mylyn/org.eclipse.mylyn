/******************************************************************************
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
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core.pr;

import java.util.List;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryCommit;

/**
 * Pull request composite that includes commits
 */
public class PullRequestComposite {

	private PullRequest request;

	private List<RepositoryCommit> commits;

	/**
	 * @return request
	 */
	public PullRequest getRequest() {
		return request;
	}

	/**
	 * @param request
	 * @return this pull request composite
	 */
	public PullRequestComposite setRequest(PullRequest request) {
		this.request = request;
		return this;
	}

	/**
	 * @return commits
	 */
	public List<RepositoryCommit> getCommits() {
		return commits;
	}

	/**
	 * @param commits
	 * @return this pull request composite
	 */
	public PullRequestComposite setCommits(List<RepositoryCommit> commits) {
		this.commits = commits;
		return this;
	}
}
