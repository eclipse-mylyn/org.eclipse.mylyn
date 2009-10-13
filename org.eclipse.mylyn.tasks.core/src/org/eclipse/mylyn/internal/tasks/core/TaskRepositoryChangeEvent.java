/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.EventObject;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TaskRepositoryChangeEvent extends EventObject {

	private static final long serialVersionUID = -8177578930986469693L;

	private final TaskRepository repository;

	private final TaskRepositoryDelta delta;

	public TaskRepositoryChangeEvent(Object source, TaskRepository repository, TaskRepositoryDelta delta) {
		super(source);
		Assert.isNotNull(source);
		Assert.isNotNull(repository);
		Assert.isNotNull(delta);
		this.repository = repository;
		this.delta = delta;
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public TaskRepositoryDelta getDelta() {
		return delta;
	}

}
