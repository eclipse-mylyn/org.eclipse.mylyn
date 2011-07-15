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
package org.eclipse.egit.github.core;

import java.util.List;

/**
 * Pull request commit model class.
 */
public class PullRequestCommit {

	private Commit commit;

	private List<Commit> parents;

	private String sha;

	private String url;

	private User author;

	private User committer;

	/**
	 * @return commit
	 */
	public Commit getCommit() {
		return commit;
	}

	/**
	 * @param commit
	 * @return this commit
	 */
	public PullRequestCommit setCommit(Commit commit) {
		this.commit = commit;
		return this;
	}

	/**
	 * @return parents
	 */
	public List<Commit> getParents() {
		return parents;
	}

	/**
	 * @param parents
	 * @return this commit
	 */
	public PullRequestCommit setParents(List<Commit> parents) {
		this.parents = parents;
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
	 * @return this commit
	 */
	public PullRequestCommit setSha(String sha) {
		this.sha = sha;
		return this;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 * @return this commit
	 */
	public PullRequestCommit setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * @return author
	 */
	public User getAuthor() {
		return author;
	}

	/**
	 * @param author
	 * @return this commit
	 */
	public PullRequestCommit setAuthor(User author) {
		this.author = author;
		return this;
	}

	/**
	 * @return committer
	 */
	public User getCommitter() {
		return committer;
	}

	/**
	 * @param committer
	 * @return this commit
	 */
	public PullRequestCommit setCommitter(User committer) {
		this.committer = committer;
		return this;
	}
}
