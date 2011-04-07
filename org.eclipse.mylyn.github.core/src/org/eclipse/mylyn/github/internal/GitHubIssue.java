/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;


/**
 * GitHub Issue object to hold all the properties of an individual issue.
 */
public class GitHubIssue {

	private String number;

	private String user;

	private String title;

	private String body;

	private String comment_new;

	private int comments;

	/**
	 * open, closed
	 */
	private String state;
	
	private String created_at;
	private String updated_at;
	private String closed_at;
	
	/**
	 * Create a new GitHub Issue Object
	 * 
	 * @param number
	 *            - GitHub Issue number
	 * @param user
	 *            - User who the posted issue belongs too.
	 * @param title
	 *            - Issue title
	 * @param body
	 *            - The text body of the issue;
	 */
	public GitHubIssue(final String number, final String user,
			final String title, final String body) {
		this.number = number;
		this.user = user;
		this.title = title;
		this.body = body;
		this.comment_new = null;
	}

	/**
	 * Create a GitHub Issue with all parameters set to empty.
	 */
	public GitHubIssue() {
		this.number = "";
		this.user = "";
		this.title = "";
		this.body = "";
		this.comment_new = null;
	}

	/**
	 * Getter for the issue number
	 * 
	 * @return The string representation of the issue number.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Set the issues's number
	 * 
	 * @param number
	 *            - String representation of the number to set to.
	 */
	public void setNumber(final String number) {
		this.number = number;
	}

	/**
	 * Getter for the user name of the issue creator
	 * 
	 * @return The user name of the person who created the issue
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the issue user name to
	 * 
	 * @param user
	 *            - The user name to set the issue creator to.
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	/**
	 * Getter for the issue Title
	 * 
	 * @return The title text of this issue
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Getter of the body of an issue
	 * 
	 * @return The text body of the issue
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Setter for the body of an issue
	 * 
	 * @param body
	 *            - The text body to set for this issue
	 */
	public void setBody(final String body) {
		this.body = body;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getClosed_at() {
		return closed_at;
	}

	public void setClosed_at(String closed_at) {
		this.closed_at = closed_at;
	}	

	public String getComment_new() {
		return comment_new;
	}

	public void setComment_new(String comment_new) {
		this.comment_new = comment_new;
	}

	/**
	 * Get number of comments issue has
	 *
	 * @return comments
	 */
	public int getComments() {
		return this.comments;
	}

	/**
	 * Set number of comments that issue has
	 *
	 * @param comments
	 */
	public void setComments(int comments) {
		this.comments = comments;
	}

}
