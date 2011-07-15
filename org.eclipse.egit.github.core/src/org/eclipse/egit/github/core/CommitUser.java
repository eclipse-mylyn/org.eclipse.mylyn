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

import java.util.Date;

/**
 * 
 */
public class CommitUser {

	private Date date;

	private String email;

	private String name;

	/**
	 * @return date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 * @return this commit user
	 */
	public CommitUser setDate(Date date) {
		this.date = date;
		return this;
	}

	/**
	 * @return email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 * @return this commit user
	 */
	public CommitUser setEmail(String email) {
		this.email = email;
		return this;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @return this commit user
	 */
	public CommitUser setName(String name) {
		this.name = name;
		return this;
	}
}
