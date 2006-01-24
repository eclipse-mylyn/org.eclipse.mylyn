/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui;

/**
 * Class to hold information about opening a bug report, such as what comment
 * number to jump to
 * 
 * @author Shawn Minto
 */
public class BugzillaOpenStructure {

	private String server;

	private int bugId;

	private int commentNumber;

	/**
	 * Constructor
	 * 
	 * @param server
	 *            The server that the bug resides on
	 * @param bugId
	 *            The id of the bug
	 * @param commentNumber
	 *            The comment number to jump to when opened, or -1
	 */
	public BugzillaOpenStructure(String server, int bugId, int commentNumber) {
		this.bugId = bugId;
		this.commentNumber = commentNumber;
		this.server = server;
	}

	/**
	 * Get the bug id to open
	 * 
	 * @return The bug id
	 */
	public Integer getBugId() {
		return bugId;
	}

	/**
	 * Get the comment number to jump to
	 * 
	 * @return The comment number or -1 if none
	 */
	public Integer getCommentNumber() {
		return commentNumber;
	}

	/**
	 * Get the server the bug resides on
	 * 
	 * @return The server url string
	 */
	public String getServer() {
		return server;
	}
}