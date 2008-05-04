/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;

/**
 * A selection of an element in a view.
 */
// API 3.0 deprecate
public class RepositoryTaskSelection implements IRepositoryTaskSelection {

	protected String id;

	protected String repositoryUrl;

	/** The contents of the selection. */
	protected String contents;

	protected String taskSummary;

	protected String repositoryKind;

	/**
	 * The comment, if a comment was selected. If the selection was not a comment, then this is <code>null</code>.
	 */
	protected TaskComment taskComment;

	/**
	 * Creates a new <code>RepositoryTaskSelection</code> with no supplied comment.
	 * 
	 * @param taskId
	 *            The taskId of the Bugzilla object that the selection was on.
	 * @param server
	 *            The server of the Bugzilla object that the selection was on.
	 * @param contents
	 *            The contents of the selection.
	 */
	public RepositoryTaskSelection(String id, String server, String kind, String contents, boolean isDescription,
			String summary) {
		this(id, server, kind, contents, null, summary);
		this.isDescription = isDescription;
	}

	/**
	 * Creates a new <code>RepositoryTaskSelection</code>.
	 * 
	 * @param taskId
	 *            The taskId of the Bugzilla object that the selection was on.
	 * @param server
	 *            The server of the Bugzilla object that the selection was on.
	 * @param contents
	 *            The contents of the selection.
	 * @param taskComment
	 *            The <code>Comment</code> object for this selection. If a comment was not selected, then this should
	 *            be <code>null</code>.
	 */
	public RepositoryTaskSelection(String id, String server, String kind, String contents, TaskComment taskComment,
			String summary) {
		this.id = id;
		this.repositoryUrl = server;
		this.repositoryKind = kind;
		this.contents = contents;
		this.taskComment = taskComment;
		this.taskSummary = summary;
	}

	// /**
	// * Creates a new <code>RepositoryTaskSelection</code> with no supplied
	// * contents.
	// *
	// * @param taskId
	// * The taskId of the Bugzilla object that the selection was on.
	// * @param server
	// * The server of the Bugzilla object that the selection was on.
	// * @param taskComment
	// * The <code>Comment</code> object for this selection. If a
	// * comment was not selected, then this should be
	// * <code>null</code>.
	// */
	// public RepositoryTaskSelection(String id, String server, TaskComment
	// taskComment, String summary) {
	// this(id, server, null, taskComment, summary);
	// }

	public boolean hasComment() {
		return taskComment != null;
	}

	public TaskComment getComment() {
		return taskComment;
	}

	public void setComment(TaskComment taskComment) {
		this.taskComment = taskComment;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getRepositoryKind() {
		return repositoryKind;
	}

	public void setServer(String server) {
		this.repositoryUrl = server;
	}

	public boolean isEmpty() {
		return (repositoryUrl == null) || ((contents == null) && (taskComment == null));
	}

	private boolean isCommentHeader = false;

	private boolean isDescription = false;

	public boolean isCommentHeader() {
		return isCommentHeader;
	}

	public boolean isDescription() {
		return isDescription;
	}

	public void setIsCommentHeader(boolean isCommentHeader) {
		this.isCommentHeader = isCommentHeader;
	}

	public void setIsDescription(boolean isDescription) {
		this.isDescription = isDescription;
	}

	public String getBugSummary() {
		return taskSummary;
	}
}
