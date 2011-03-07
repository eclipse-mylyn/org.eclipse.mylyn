/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.dsl;

/**
 * 
 * @author mattk
 * 
 */
public class ReviewDslAttachmentScopeItem extends ReviewDslScopeItem {

	public enum Type {
		PATCH, RESOURCE
	}

	private Type type;
	private String fileName;
	private String author;
	private String createdDate;
	private String taskId;

	public ReviewDslAttachmentScopeItem(Type type, String fileName,
			String author, String createdDate, String taskId) {
		super();
		this.type = type;
		this.fileName = fileName;
		this.author = author;
		this.createdDate = createdDate;
		this.taskId = taskId;
	}

	public Type getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}

	public String getAuthor() {
		return author;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public String getTaskId() {
		return taskId;
	}

	@Override
	public StringBuilder serialize(StringBuilder sb) {
		sb.append(type == Type.PATCH ? "Patch" : "Resource");
		sb.append("from Attachment \"");
		sb.append(fileName);
		sb.append("\" by \"");
		sb.append(author);
		sb.append("\" on \"");
		sb.append(createdDate);
		sb.append("\" of task ");
		sb.append(taskId);
		return sb;
	}

}
