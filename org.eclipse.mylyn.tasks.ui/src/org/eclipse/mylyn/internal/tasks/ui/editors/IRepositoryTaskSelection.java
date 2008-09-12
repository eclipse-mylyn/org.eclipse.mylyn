/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public interface IRepositoryTaskSelection extends ISelection {

	/**
	 * @return <code>true</code> if a comment was selected.
	 */
	public boolean hasComment();

	/**
	 * @return the <code>Comment</code> object for this selection, or <code>null</code> if a comment was not selected.
	 */
	public TaskComment getComment();

	/**
	 * Sets the <code>Comment</code> object for this selection. If a comment was not selected, then this should be
	 * <code>null</code>.
	 * 
	 * @param taskComment
	 *            The selection's comment, or <code>null</code> if not applicable.
	 */
	public void setComment(TaskComment taskComment);

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
	 * @return The taskId of the Bugzilla object that the selection was on.
	 */
	public String getId();

	/**
	 * Sets the taskId of the Bugzilla object that the selection was on.
	 * 
	 * @param taskId
	 *            The taskId of the bug.
	 */
	public void setId(String id);

	/**
	 * @return The server of the Bugzilla object that the selection was on, or <code>null</code> if no server is
	 *         supplied.
	 */
	public String getRepositoryUrl();

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
