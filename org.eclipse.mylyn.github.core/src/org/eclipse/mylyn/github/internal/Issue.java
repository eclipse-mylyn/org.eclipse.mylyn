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
 * GitHub issue class.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Issue {

	private Date closedAt;

	private Date createdAt;

	private Date updatedAt;

	private int comments;

	private int number;

	private List<Label> labels;

	private Milestone milestone;

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
		return this.closedAt;
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
	 * @return labels
	 */
	public List<Label> getLabels() {
		return this.labels;
	}

	/**
	 * @return milestone
	 */
	public Milestone getMilestone() {
		return this.milestone;
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
	 * @return body
	 */
	public String getBody() {
		return this.body;
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
		return this.htmlUrl;
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
		return this.url;
	}

	/**
	 * @return assignee
	 */
	public User getAssignee() {
		return this.assignee;
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
		return this.user;
	}

}
