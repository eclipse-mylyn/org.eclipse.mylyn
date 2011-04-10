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

/**
 * GitHub issue milestone class.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Milestone {

	private Date createdAt;

	private Date dueOn;

	private int closedIssues;

	private int number;

	private int openIssues;

	private String description;

	private String state;

	private String title;

	private String url;

	private User creator;

	/**
	 * @return createdAt
	 */
	public Date getCreatedAt() {
		return this.createdAt;
	}

	/**
	 * @return dueOn
	 */
	public Date getDueOn() {
		return this.dueOn;
	}

	/**
	 * @return closedIssues
	 */
	public int getClosedIssues() {
		return this.closedIssues;
	}

	/**
	 * @return number
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * @param number
	 * @return this milestone
	 */
	public Milestone setNumber(int number) {
		this.number = number;
		return this;
	}

	/**
	 * @return openIssues
	 */
	public int getOpenIssues() {
		return this.openIssues;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return this.description;
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
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @return creator
	 */
	public User getCreator() {
		return this.creator;
	}

}
