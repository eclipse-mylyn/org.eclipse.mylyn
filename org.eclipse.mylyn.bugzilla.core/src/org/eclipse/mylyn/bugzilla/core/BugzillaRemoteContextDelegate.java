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

package org.eclipse.mylar.bugzilla.core;

import java.util.Date;

import org.eclipse.mylar.provisional.tasklist.IRemoteContextDelegate;

/**
 * @author Rob Elves
 * TODO: Find a better spot for this
 */
public class BugzillaRemoteContextDelegate implements IRemoteContextDelegate {

	private Comment comment;
	
	public BugzillaRemoteContextDelegate(Comment comment) {
		this.comment = comment;
	}
	public Date getDate() {
		return comment.getCreated();
	}

	public String getAuthor() {
		return comment.getAuthor();
	}

	public String getComment() {
		return comment.getText().trim();
	}

	public int getId() {
		return comment.getAttachmentId();
	}
	
}
