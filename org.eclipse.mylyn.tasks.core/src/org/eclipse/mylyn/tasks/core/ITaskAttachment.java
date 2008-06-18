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
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITaskAttachment {

	/**
	 * @since 3.0
	 */
	public abstract IRepositoryPerson getAuthor();

	/**
	 * @since 3.0
	 */
	public abstract String getComment();

	/**
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	/**
	 * @since 3.0
	 */
	public abstract String getContentType();

	/**
	 * @since 3.0
	 */
	public abstract Date getCreationDate();

	/**
	 * @since 3.0
	 */
	public abstract String getDescription();

	/**
	 * @since 3.0
	 */
	public abstract String getFileName();

	/**
	 * @since 3.0
	 */
	public abstract long getLength();

	/**
	 * @since 3.0
	 */
	public abstract String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	public abstract ITask getTask();

	/**
	 * @since 3.0
	 */
	public abstract TaskAttribute getTaskAttribute();

	/**
	 * @since 3.0
	 */
	public abstract TaskRepository getTaskRepository();

	/**
	 * @since 3.0
	 */
	public abstract String getUrl();

	/**
	 * @since 3.0
	 */
	public abstract boolean isDeprecated();

	/**
	 * @since 3.0
	 */
	public abstract boolean isPatch();

	/**
	 * @since 3.0
	 */
	public abstract void setAuthor(IRepositoryPerson author);

	/**
	 * @since 3.0
	 */
	public abstract void setContentType(String contentType);

	/**
	 * @since 3.0
	 */
	public abstract void setCreationDate(Date creationDate);

	/**
	 * @since 3.0
	 */
	public abstract void setDeprecated(boolean deprecated);

	/**
	 * @since 3.0
	 */
	public abstract void setDescription(String description);

	/**
	 * @since 3.0
	 */
	public abstract void setFileName(String fileName);

	/**
	 * @since 3.0
	 */
	public abstract void setLength(long length);

	/**
	 * @since 3.0
	 */
	public abstract void setPatch(boolean patch);

	/**
	 * @since 3.0
	 */
	public abstract void setUrl(String url);

}