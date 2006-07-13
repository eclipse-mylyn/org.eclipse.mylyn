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

package org.eclipse.mylar.tasks.core;

import java.io.Serializable;

/**
 * A comment posted on a bug.
 * 
 * @author Rob Elves (revisions for bug 136219)
 */
public class TaskComment extends AttributeContainer implements Serializable {

	private static final long serialVersionUID = -1760372869047050979L;

	// /** Parser for dates in the report */
	// public static SimpleDateFormat creation_ts_date_format = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm");

	/** Comment's bug */
	private final RepositoryTaskData bug;

	/** Comment's number */
	private final int number;

	// /** Comment's creation timestamp */
	// private Date created;

	/** Preceding comment */
	private TaskComment previous;

	/** Following comment */
	private TaskComment next;

	private boolean hasAttachment;

	private int attachmentId;

	public TaskComment(AbstractAttributeFactory attributeFactory, RepositoryTaskData report, int num) {
		super(attributeFactory);
		this.bug = report;
		this.number = num;
	}

	/**
	 * Get the bug that this comment is associated with
	 * 
	 * @return The bug that this comment is associated with
	 */
	public RepositoryTaskData getBug() {
		return bug;
	}

	/**
	 * Get this comment's number
	 * 
	 * @return This comment's number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Get the time that this comment was created
	 * 
	 * @return The comments creation timestamp
	 */
	public String getCreated() {
		return getAttributeValue(RepositoryTaskAttribute.COMMENT_DATE);
		// TaskRepository repository =
		// MylarTaskListPlugin.getRepositoryManager().getRepository(bug.getRepositoryKind(),
		// bug.getRepositoryUrl());
		// TimeZone timeZone = TimeZone.getDefault();
		// if(repository != null) {
		// timeZone = TimeZone.getTimeZone(repository.getTimeZoneId());
		// }
		// if (created == null) {
		// created = Calendar.getInstance().getTime();
		// try {
		// creation_ts_date_format.setTimeZone(timeZone);
		// created =
		// creation_ts_date_format.parse(getAttributeValue(RepositoryTaskAttribute.COMMENT_DATE));
		// } catch (Exception e) {
		// // ignore
		// // MylarStatusHandler.log("Comment creation date parse error,
		// // setting to NOW.", Comment.class);
		// }
		// }
		// return created;
	}

	/**
	 * Get the author of the comment
	 * 
	 * @return The comments author
	 */
	public String getAuthor() {
		return getAttributeValue(RepositoryTaskAttribute.USER_OWNER);
	}

	/**
	 * Get the authors real name
	 * 
	 * @return Returns author's name, or <code>null</code> if not known
	 */
	public String getAuthorName() {
		// TODO: Currently we don't get the real name from the xml.
		// Need retrieve these names somehow
		return getAuthor();
	}

	/**
	 * Get the text contained in the comment
	 * 
	 * @return The comments text
	 */
	public String getText() {
		return getAttributeValue(RepositoryTaskAttribute.COMMENT_TEXT);
	}

	/**
	 * Get the next comment for the bug
	 * 
	 * @return Returns the following comment, or <code>null</code> if the last
	 *         one.
	 */
	public TaskComment getNext() {
		return next;
	}

	/**
	 * Set the next comment for the bug
	 * 
	 * @param next
	 *            The comment that is after this one
	 */
	protected void setNext(TaskComment next) {
		this.next = next;
	}

	/**
	 * Get the previous comment
	 * 
	 * @return Returns preceding comment, or <code>null</code> if the first
	 *         one
	 */
	public TaskComment getPrevious() {
		return previous;
	}

	/**
	 * Seth the previous comment for the bug
	 * 
	 * @param previous
	 *            The comment that is before this one
	 */
	protected void setPrevious(TaskComment previous) {
		this.previous = previous;
	}

	public void setHasAttachment(boolean b) {
		this.hasAttachment = b;
	}

	public boolean hasAttachment() {
		return hasAttachment;
	}

	public void setAttachmentId(int attachmentID) {
		this.attachmentId = attachmentID;
	}

	public int getAttachmentId() {
		return attachmentId;
	}
}
