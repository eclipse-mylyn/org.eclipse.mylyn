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
 *    See git history
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.egit.github.core;

import java.io.Serializable;
import java.util.List;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GitUser;

/**
 * Repository commit model class. This class contains GitHub-specific information about the commit and also provides access to the raw Git
 * {@link Commit} object.
 */
public class RepositoryCommit implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -8911733018395257250L;

	private GHCommit commit;

	private CommitStats stats;

	private List<GHCommit> parents;

	private List<GHCommit.File> files;

	private String sha;

	private String url;

	private GitUser author;

	private GitUser committer;

	/**
	 * @return commit
	 */
	public GHCommit getCommit() {
		return commit;
	}

	/**
	 * @param commit
	 * @return this commit
	 */
	public RepositoryCommit setCommit(GHCommit commit) {
		this.commit = commit;
		return this;
	}

	/**
	 * @return stats
	 */
	public CommitStats getStats() {
		return stats;
	}

	/**
	 * @param stats
	 * @return this commit
	 */
	public RepositoryCommit setStats(CommitStats stats) {
		this.stats = stats;
		return this;
	}

	/**
	 * @return parents
	 */
	public List<GHCommit> getParents() {
		return parents;
	}

	/**
	 * @param parents
	 * @return this commit
	 */
	public RepositoryCommit setParents(List<GHCommit> parents) {
		this.parents = parents;
		return this;
	}

	/**
	 * @return files
	 */
	public List<GHCommit.File> getFiles() {
		return files;
	}

	/**
	 * @param files
	 * @return this commit
	 */
	public RepositoryCommit setFiles(List<GHCommit.File> files) {
		this.files = files;
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
	public RepositoryCommit setSha(String sha) {
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
	public RepositoryCommit setUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * @return author
	 */
	public GitUser getAuthor() {
		return author;
	}

	/**
	 * @param gitUser
	 * @return this commit
	 */
	public RepositoryCommit setAuthor(GitUser gitUser) {
		author = gitUser;
		return this;
	}

	/**
	 * @return committer
	 */
	public GitUser getCommitter() {
		return committer;
	}

	/**
	 * @param committer
	 * @return this commit
	 */
	public RepositoryCommit setCommitter(GitUser committer) {
		this.committer = committer;
		return this;
	}
}
