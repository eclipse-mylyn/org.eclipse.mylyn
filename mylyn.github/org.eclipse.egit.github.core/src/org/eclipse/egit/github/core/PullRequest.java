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
import java.util.List;

/**
 * Pull request model class.
 */
public class PullRequest {

	private Date closedAt;
	private Date createdAt;
	private Date issueCreatedAt;
	private Date issueUpdatedAt;
	private Date mergedAt;
	private Date updatedAt;

	private double position;

	private int comments;
	private int number;
	private int votes;

	private List<PullRequestDiscussion> discussion;
	private List<String> labels;

	private PullRequestMarker base;
	private PullRequestMarker head;

	private String body;
	private String diffUrl;
	private String htmlUrl;
	private String patchUrl;
	private String state;
	private String title;

	private User issueUser;
	private User user;

	/**
	 * @return closedAt
	 */
	public Date getClosedAt() {
		return this.closedAt;
	}

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * @return issueCreatedAt
	 */
	public Date getIssueCreatedAt() {
		return this.issueCreatedAt;
	}

	/**
	 * @return issueUpdatedAt
	 */
	public Date getIssueUpdatedAt() {
		return this.issueUpdatedAt;
	}

	/**
	 * @return mergedAt
	 */
	public Date getMergedAt() {
		return this.mergedAt;
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
	public double getPosition() {
		return this.position;
	}

	/**
	 * @return comments
	 */
	public int getComments() {
		return this.comments;
	}

	/**
	 * @return number
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * @return votes
	 */
	public int getVotes() {
		return this.votes;
	}

	/**
	 * @return discussion
	 */
	public List<PullRequestDiscussion> getDiscussion() {
		return this.discussion;
	}

	/**
	 * @return labels
	 */
	public List<String> getLabels() {
		return this.labels;
	}

	/**
	 * @return base
	 */
	public PullRequestMarker getBase() {
		return this.base;
	}

	/**
	 * @return head
	 */
	public PullRequestMarker getHead() {
		return this.head;
	}

	/**
	 * @return body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * @return diffUrl
	 */
	public String getDiffUrl() {
		return this.diffUrl;
	}

	/**
	 * @return htmlUrl
	 */
	public String getHtmlUrl() {
		return this.htmlUrl;
	}

	/**
	 * @return patchUrl
	 */
	public String getPatchUrl() {
		return this.patchUrl;
	}

	/**
	 * @return state
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @return issueUser
	 */
	public User getIssueUser() {
		return this.issueUser;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return this.user;
	}

}
