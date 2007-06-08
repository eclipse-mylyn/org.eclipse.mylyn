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

package org.eclipse.mylyn.tasks.core;

import java.io.Serializable;

/**
 * A comment posted on a bug.
 * 
 * @author Rob Elves (revisions for bug 136219)
 */
public class TaskComment extends AttributeContainer implements Serializable {

	private static final long serialVersionUID = 1076016406335550318L;

	/** Comment's number */
	private final int number;

	private boolean hasAttachment;

	private String attachmentId;

	public TaskComment(AbstractAttributeFactory attributeFactory, int num) {
		super(attributeFactory);
		this.number = num;
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

	public void setHasAttachment(boolean b) {
		this.hasAttachment = b;
	}

	public boolean hasAttachment() {
		return hasAttachment;
	}

	public void setAttachmentId(String attachmentID) {
		this.attachmentId = attachmentID;
	}

	public String getAttachmentId() {
		return attachmentId;
	}
}
