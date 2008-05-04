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
 */
public interface ITaskComment {

	public abstract ITaskRepositoryPerson getAuthor();

	public abstract String getConnectorKind();

	public abstract Date getCreationDate();

	public abstract int getNumber();

	public abstract String getRepositoryUrl();

	public abstract AbstractTask getTask();

	public abstract TaskAttribute getTaskAttribute();

	public abstract TaskRepository getTaskRepository();

	public abstract String getText();

	public abstract String getUrl();

	public abstract void setAuthor(ITaskRepositoryPerson author);

	public abstract void setCreationDate(Date creationDate);

	public abstract void setText(String text);

	public abstract void setUrl(String url);

}