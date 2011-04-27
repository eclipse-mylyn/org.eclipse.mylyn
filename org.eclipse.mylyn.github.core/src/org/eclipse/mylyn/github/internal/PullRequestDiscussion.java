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
import java.util.List;

/**
 * Pull request discussion model class.
 */
public class PullRequestDiscussion {

	/**
	 * TYPE_COMMIT
	 */
	public static final String TYPE_COMMIT = "Commit";

	/**
	 * TYPE_ISSUE_COMMENT
	 */
	public static final String TYPE_ISSUE_COMMENT = "IssueComment";

	private Date authoredDate;
	private Date commitedDate;
	private Date createdAt;
	private Date updatedAt;

	private int position;

	private List<Id> parents;

	private String body;
	private String commitId;
	private String id;
	private String diffHunk;
	private String gravatarId;
	private String message;
	private String originalCommitId;
	private String path;
	private String tree;
	private String type;

	private User author;
	private User committer;
	private User user;

	/**
	 * @return authoredDate
	 */
	public Date getAuthoredDate() {
		return this.authoredDate;
	}

	/**
	 * @return commitedDate
	 */
	public Date getCommitedDate() {
		return this.commitedDate;
	}

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * @return updatedAt
	 */
	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	/**
	 * @return position
	 */
	public int getPosition() {
		return this.position;
	}

	/**
	 * @return parents
	 */
	public List<Id> getParents() {
		return this.parents;
	}

	/**
	 * @return body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * @return commitId
	 */
	public String getCommitId() {
		return this.commitId;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return diffHunk
	 */
	public String getDiffHunk() {
		return this.diffHunk;
	}

	/**
	 * @return gravatarId
	 */
	public String getGravatarId() {
		return this.gravatarId;
	}

	/**
	 * @return message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @return originalCommitId
	 */
	public String getOriginalCommitId() {
		return this.originalCommitId;
	}

	/**
	 * @return path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return tree
	 */
	public String getTree() {
		return this.tree;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return author
	 */
	public User getAuthor() {
		return this.author;
	}

	/**
	 * @return committer
	 */
	public User getCommitter() {
		return this.committer;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

}
