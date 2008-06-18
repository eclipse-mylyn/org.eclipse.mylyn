/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import java.io.Serializable;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public final class RepositoryAttachment extends AttributeContainer implements Serializable {

	private static final long serialVersionUID = 2663237137799050826L;

	private boolean isPatch = false;

	private boolean isObsolete = false;

	private String creator = "";

	private String repositoryUrl;

	private String repositoryKind;

	private String taskId;

	public RepositoryAttachment(AbstractAttributeFactory attributeFactory) {
		super(attributeFactory);
	}

	public boolean isObsolete() {
		return isObsolete;
	}

	public void setObsolete(boolean isObsolete) {
		this.isObsolete = isObsolete;
	}

	/**
	 * Get the time that this attachment was posted
	 * 
	 * @return The attachment's creation timestamp
	 */
	public String getDateCreated() {
		return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_DATE);
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @since 2.2
	 * @return The name of the file, e.g. screenshot.jpg.
	 */
	public String getFilename() {
		return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
	}

	public String getDescription() {
		return getAttributeValue(RepositoryTaskAttribute.DESCRIPTION);
	}

	public String getId() {
		return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_ID);
	}

	public String getUrl() {
		return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_URL);
	}

	public String getContentType() {
		// I've seen both "ctype" and "type" occur for this, investigate
		if (getAttribute(RepositoryTaskAttribute.ATTACHMENT_TYPE) != null) {
			return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_TYPE);
		} else {
			return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_CTYPE);
		}
	}

	public String getSize() {
		return getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_SIZE);
	}

	public boolean isPatch() {
		return isPatch;
	}

	public void setPatch(boolean b) {
		isPatch = b;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getRepositoryKind() {
		return repositoryKind;
	}

	public void setRepositoryKind(String repositoryKind) {
		this.repositoryKind = repositoryKind;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
