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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.mylar.internal.bugzilla.core.internal.BugzillaReportElement;

/**
 * A comment posted on a bug.
 * 
 * @author Rob Elves (revisions for bug 136219)
 */
public class Comment extends AttributeContainer implements Serializable {

	private static final long serialVersionUID = -1760372869047050979L;

	/** Parser for dates in the report */
	public static SimpleDateFormat creation_ts_date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/** Comment's bug */
	private final AbstractRepositoryReport bug;

	/** Comment's number */
	private final int number;

	// /** Comment's text */
	// private String text;
	//
	// /** Comment's author */
	// private final String author;
	//
	// /** Author's realname, if known */
	// private final String authorName;

	/** Comment's creation timestamp */
	private Date created;

	/** Preceding comment */
	private Comment previous;

	/** Following comment */
	private Comment next;

	// private boolean hasAttachment = false;
	//
	// private int attachmentId = -1;
	//
	// private String attachmentDescription = "";
	//
	// private boolean obsolete = false;

	public Comment(AbstractRepositoryReport report, int num) {
		this.bug = report;
		this.number = num;
	}

	// /**
	// * Constructor
	// *
	// * @param bug
	// * The bug that this comment is associated with
	// * @param date
	// * The date taht this comment was entered on
	// * @param author
	// * The author of the bug
	// * @param authorName
	// * The authors real name
	// * @depricated This use Comment(AbstractRepositoryReport report, int num)
	// instead
	// */
	// public Comment(AbstractRepositoryReport bug, int number, Date date,
	// String author, String authorName) {
	// this.bug = bug;
	// this.number = number;
	// // this.created = date;
	// // this.author = author;
	// // this.authorName = authorName;
	// }

	/**
	 * Get the bug that this comment is associated with
	 * 
	 * @return The bug that this comment is associated with
	 */
	public AbstractRepositoryReport getBug() {
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
	public Date getCreated() {
		if (created == null) {
			created = Calendar.getInstance().getTime();
			try {
				created = creation_ts_date_format.parse(getAttributeValue(BugzillaReportElement.BUG_WHEN));
			} catch (Exception e) {
				// ignore
				// MylarStatusHandler.log("Comment creation date parse error,
				// setting to NOW.", Comment.class);
			}
		}
		return created;
	}

	/**
	 * Get the author of the comment
	 * 
	 * @return The comments author
	 */
	public String getAuthor() {
		return getAttributeValue(BugzillaReportElement.WHO);
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
		return getAttributeValue(BugzillaReportElement.THETEXT);
	}

	// /**
	// * Set the comments text
	// *
	// * @param text
	// * The text to set the comment to have
	// */
	// public void setText(String text) {
	// this.text = text;
	// }

	/**
	 * Get the next comment for the bug
	 * 
	 * @return Returns the following comment, or <code>null</code> if the last
	 *         one.
	 */
	public Comment getNext() {
		return next;
	}

	/**
	 * Set the next comment for the bug
	 * 
	 * @param next
	 *            The comment that is after this one
	 */
	protected void setNext(Comment next) {
		this.next = next;
	}

	/**
	 * Get the previous comment
	 * 
	 * @return Returns preceding comment, or <code>null</code> if the first
	 *         one
	 */
	public Comment getPrevious() {
		return previous;
	}

	/**
	 * Seth the previous comment for the bug
	 * 
	 * @param previous
	 *            The comment that is before this one
	 */
	protected void setPrevious(Comment previous) {
		this.previous = previous;
	}

	// public void setHasAttachment(boolean b) {
	// this.hasAttachment = b;
	// }
	//
	// public boolean hasAttachment() {
	// return hasAttachment;
	// }
	//	
	// public void setAttachmentId(int attachmentID) {
	// this.attachmentId = attachmentID;
	// }
	//
	// public int getAttachmentId() {
	// return attachmentId;
	// }
	//
	// public void setAttachmentDescription(String attachmentDescription) {
	// this.attachmentDescription = attachmentDescription;
	// }
	//	
	// public String getAttachmentDescription() {
	// return attachmentDescription;
	// }
	//
	// public void setObsolete(boolean obsolete) {
	// this.obsolete = obsolete;
	// }
	//	
	// public boolean isObsolete() {
	// return obsolete;
	// }
	//

}
