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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.provisional.bugzilla.core.Comment;

/**
 * Interface for a selection of a Bugzilla element in a view.
 */
public interface IBugzillaReportSelection extends ISelection {

	/**
	 * @return <code>true</code> if a comment was selected.
	 */
	public boolean hasComment();

	/**
	 * @return the <code>Comment</code> object for this selection, or
	 *         <code>null</code> if a comment was not selected.
	 */
	public Comment getComment();

	/**
	 * Sets the <code>Comment</code> object for this selection. If a comment
	 * was not selected, then this should be <code>null</code>.
	 * 
	 * @param comment
	 *            The selection's comment, or <code>null</code> if not
	 *            applicable.
	 */
	public void setComment(Comment comment);

	/**
	 * @return The contents of the selection. This can be <code>null</code>.
	 */
	public String getContents();

	/**
	 * Sets the contents of the selection.
	 * 
	 * @param contents
	 *            The selection.
	 */
	public void setContents(String contents);

	/**
	 * @return The id of the Bugzilla object that the selection was on.
	 */
	public int getId();

	/**
	 * Sets the id of the Bugzilla object that the selection was on.
	 * 
	 * @param id
	 *            The id of the bug.
	 */
	public void setId(int id);

	/**
	 * @return The server of the Bugzilla object that the selection was on, or
	 *         <code>null</code> if no server is supplied.
	 */
	public String getServer();

	/**
	 * Sets the server of the Bugzilla object that the selection was on.
	 * 
	 * @param server
	 *            The server of the bug.
	 */
	public void setServer(String server);

	public boolean isCommentHeader();

	public boolean isDescription();

	public String getBugSummary();

}
