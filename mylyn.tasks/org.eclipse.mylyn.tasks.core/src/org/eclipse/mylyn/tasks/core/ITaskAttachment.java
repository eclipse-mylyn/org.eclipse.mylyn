/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITaskAttachment {

	/**
	 * @since 3.0
	 */
	IRepositoryPerson getAuthor();

	/**
	 * @since 3.0
	 */
	String getComment();

	/**
	 * @since 3.0
	 */
	String getConnectorKind();

	/**
	 * @since 3.0
	 */
	String getContentType();

	/**
	 * @since 3.0
	 */
	Date getCreationDate();

	/**
	 * @since 3.0
	 */
	String getDescription();

	/**
	 * @since 3.0
	 */
	String getFileName();

	/**
	 * @since 3.0
	 */
	long getLength();

	/**
	 * @since 3.0
	 */
	String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	ITask getTask();

	/**
	 * @since 3.0
	 */
	TaskAttribute getTaskAttribute();

	/**
	 * @since 3.0
	 */
	TaskRepository getTaskRepository();

	/**
	 * @since 3.0
	 */
	String getUrl();

	/**
	 * @since 3.0
	 */
	boolean isDeprecated();

	/**
	 * @since 3.0
	 */
	boolean isPatch();

	/**
	 * @since 3.0
	 */
	void setAuthor(IRepositoryPerson author);

	/**
	 * @since 3.0
	 */
	void setContentType(String contentType);

	/**
	 * @since 3.0
	 */
	void setCreationDate(Date creationDate);

	/**
	 * @since 3.0
	 */
	void setDeprecated(boolean deprecated);

	/**
	 * @since 3.0
	 */
	void setDescription(String description);

	/**
	 * @since 3.0
	 */
	void setFileName(String fileName);

	/**
	 * @since 3.0
	 */
	void setLength(long length);

	/**
	 * @since 3.0
	 */
	void setPatch(boolean patch);

	/**
	 * @since 3.0
	 */
	void setUrl(String url);

}