/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;

public class UpdateRepositoryConfigurationJob extends TaskJob {
	AbstractRepositoryConnector connector;

	TaskRepository repository;

	private IStatus error;

	private final ITask task;

	public UpdateRepositoryConfigurationJob(@NonNull String name, @NonNull TaskRepository repository,
			@NonNull AbstractRepositoryConnector connector) {
		this(name, repository, null, connector);
	}

	public UpdateRepositoryConfigurationJob(@NonNull String name, @NonNull TaskRepository repository,
			@Nullable ITask task, @NonNull AbstractRepositoryConnector connector) {
		super(name);
		this.repository = repository;
		this.task = task;
		this.connector = connector;
	}

	@Override
	public IStatus getStatus() {
		return error;
	}

	@Override
	protected IStatus run(@NonNull IProgressMonitor monitor) {
		monitor.beginTask(Messages.UpdateRepositoryConfigurationJob_Receiving_configuration, 100);
		try {
			try {
				connector.updateRepositoryConfiguration(repository, task, subMonitorFor(monitor, 100));
			} catch (CoreException e) {
				error = e.getStatus();
			}
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(Object family) {
		return family == repository;
	}

}
