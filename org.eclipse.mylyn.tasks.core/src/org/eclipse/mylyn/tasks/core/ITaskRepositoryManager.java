/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public interface ITaskRepositoryManager {

	public abstract void addListener(ITaskRepositoryListener listener);

	public abstract void addRepository(TaskRepository repository);

	public abstract List<TaskRepository> getAllRepositories();

	public abstract Set<TaskRepository> getRepositories(String kind);

	public abstract TaskRepository getRepository(String kind, String urlString);

	public abstract AbstractRepositoryConnector getRepositoryConnector(String connectorKind);

	public abstract Collection<AbstractRepositoryConnector> getRepositoryConnectors();

	public abstract void removeListener(ITaskRepositoryListener listener);

}