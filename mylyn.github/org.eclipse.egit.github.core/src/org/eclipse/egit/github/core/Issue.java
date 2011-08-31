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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.egit.github.core.util.DateUtils;

/**
 * GitHub issue model class.
 */
public class Issue implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 6358575015023539051L;

	private Date closedAt;

	private Date createdAt;

	private Date updatedAt;

	private int comments;

	private int number;

	private List<Label> labels;

	private Milestone milestone;

	private PullRequest pullRequest;

	private String body;

	private String htmlUrl;

	private String state;

	private String title;

	private String url;

	private User assignee;

	private User user;

	/**
	 * @return closedAt
	 */
	public Date getClosedAt() {
		return DateUtils.clone(closedAt);
	}

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return DateUtils.clone(createdAt);
	}

	/**
	 * @return updatedAt
	 */
	public Date getUpdatedAt() {
		return DateUtils.clone(updatedAt);
	}

	/**
	 * @return comments
	 */
	public int getComments() {
		return comments;
	}

	/**
	 * @return number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number
	 * @return this issue
	 */
	public Issue setNumber(int number) {
		this.number = number;
		return this;
	}

	/**
	 * @return labels
	 */
	public List<Label> getLabels() {
		return labels;
	}

	/**
	 * @param labels
	 * @return this issue
	 */
	public Issue setLabels(List<Label> labels) {
		this.labels = labels != null ? new ArrayList<Label>(labels) : null;
		return this;
	}

	/**
	 * @return milestone
	 */
	public Milestone getMilestone() {
		return milestone;
	}

	/**
	 * @param milestone
	 * @return this issue
	 */
	public Issue setMilestone(Milestone milestone) {
		this.milestone = milestone;
		return this;
	}

	/**
	 * @return pullRequest
	 */
	public PullRequest getPullRequest() {
		return pullRequest;
	}

	/**
	 * @return body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 * @return this issue
	 */
	public Issue setBody(String body) {
		this.body = body;
		return this;
	}

	/**
	 * @return htmlUrl
	 */
	public String getHtmlUrl() {
		return htmlUrl;
	}

	/**
	 * @return state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 * @return this issue
	 */
	public Issue setState(String state) {
		this.state = state;
		return this;
	}

	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 * @return this issue
	 */
	public Issue setTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return assignee
	 */
	public User getAssignee() {
		return assignee;
	}

	/**
	 * @param assignee
	 * @return this issue
	 */
	public Issue setAssignee(User assignee) {
		this.assignee = assignee;
		return this;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "Issue " + number; //$NON-NLS-1$
	}
}
