/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * A comment posted by a user on a task.
 * 
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITaskComment {

	/**
	 * @since 3.0
	 */
	public abstract IRepositoryPerson getAuthor();

	/**
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	/**
	 * @since 3.0
	 */
	public abstract Date getCreationDate();

	/**
	 * @since 3.0
	 */
	public abstract int getNumber();

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
	public abstract String getText();

	/**
	 * @since 3.0
	 */
	public abstract String getUrl();

	/**
	 * @since 3.0
	 */
	public abstract void setAuthor(IRepositoryPerson author);

	/**
	 * @since 3.0
	 */
	public abstract void setCreationDate(Date creationDate);

	/**
	 * @since 3.0
	 */
	public abstract void setNumber(int number);

	/**
	 * @since 3.0
	 */
	public abstract void setText(String text);

	/**
	 * @since 3.0
	 */
	public abstract void setUrl(String url);

}