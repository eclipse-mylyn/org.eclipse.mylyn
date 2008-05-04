/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @since 3.0
 */
public interface ITaskAttachment2 {

	public abstract ITaskRepositoryPerson getAuthor();

	public abstract String getComment();

	public abstract String getConnectorKind();

	public abstract String getContentType();

	public abstract Date getCreationDate();

	public abstract String getDescription();

	public abstract String getFileName();

	public abstract long getLength();

	public abstract String getRepositoryUrl();

	public abstract AbstractTask getTask();

	public abstract TaskAttribute getTaskAttribute();

	public abstract TaskRepository getTaskRepository();

	public abstract String getUrl();

	public abstract boolean isDeprecated();

	public abstract boolean isPatch();

	public abstract void setAuthor(ITaskRepositoryPerson author);

	public abstract void setContentType(String contentType);

	public abstract void setCreationDate(Date creationDate);

	public abstract void setDeprecated(boolean deprecated);

	public abstract void setDescription(String description);

	public abstract void setFileName(String fileName);

	public abstract void setLength(long length);

	public abstract void setPatch(boolean patch);

	public abstract void setUrl(String url);

}