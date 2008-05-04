/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * @since 3.0
 */
public interface ITaskRepositoryPerson {

	public abstract String getConnectorKind();

	public abstract String getName();

	public abstract String getPersonId();

	public abstract String getRepositoryUrl();

	public abstract TaskRepository getTaskRepository();

	public abstract void setName(String name);

}