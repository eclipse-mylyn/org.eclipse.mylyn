/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.core.pr;

import java.util.List;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestCommit;

/**
 * Pull request composite that includes commits
 */
public class PullRequestComposite {

	private PullRequest request;

	private List<PullRequestCommit> commits;

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
	public List<PullRequestCommit> getCommits() {
		return commits;
	}

	/**
	 * @param commits
	 * @return this pull request composite
	 */
	public PullRequestComposite setCommits(List<PullRequestCommit> commits) {
		this.commits = commits;
		return this;
	}
}
