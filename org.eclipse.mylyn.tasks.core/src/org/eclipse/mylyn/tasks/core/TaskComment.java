/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.io.Serializable;

/**
 * A comment posted by a user on a task.
 * 
 * @author Rob Elves
 * @since 2.0
 */
public class TaskComment extends AttributeContainer implements Serializable {

	private static final long serialVersionUID = 1076016406335550318L;

	/** Comment's number */
	private int number;

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
	 * Set the comment number
	 * 
	 * @param number the number of the comment
	 */
	public void setNumber(int number) {
		this.number = number;
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
		return getAttributeValue(RepositoryTaskAttribute.COMMENT_AUTHOR);
	}

	/**
	 * Get the authors real name
	 * 
	 * @return Returns author's name, or an empty string
	 */
	public String getAuthorName() {
		return getAttributeValue(RepositoryTaskAttribute.COMMENT_AUTHOR_NAME);
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
