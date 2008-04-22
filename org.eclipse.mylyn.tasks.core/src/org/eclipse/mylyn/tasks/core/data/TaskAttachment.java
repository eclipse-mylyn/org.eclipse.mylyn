/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Date;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public class TaskAttachment {

	private final String attachmentId;

	private RepositoryPerson author;

	private String comment;

	private final String connectorKind;

	private String contentType;

	private Date creationDate;

	private boolean deprecated;

	private String description;

	private String fileName;

	private long length;

	private boolean patch;

	private final String repositoryUrl;

	private final String taskId;

	private String url;

	public TaskAttachment(String connectorKind, String repositoryUrl, String taskId, String attachmentId) {
		this.connectorKind = connectorKind;
		this.repositoryUrl = repositoryUrl;
		this.taskId = taskId;
		this.attachmentId = attachmentId;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public RepositoryPerson getAuthor() {
		return author;
	}

	public String getComment() {
		return comment;
	}

	public String getConnectorKind() {
		return connectorKind;
	}

	public String getContentType() {
		return contentType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getDescription() {
		return description;
	}

	public String getFileName() {
		return fileName;
	}

	public long getLength() {
		return length;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getUrl() {
		return url;
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	public boolean isPatch() {
		return patch;
	}

	public void setAuthor(RepositoryPerson author) {
		this.author = author;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public void setPatch(boolean patch) {
		this.patch = patch;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
