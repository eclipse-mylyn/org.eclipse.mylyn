/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.sync;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;

public class UpdateRepositoryConfigurationJob extends TaskJob {
	AbstractRepositoryConnector connector;

	TaskRepository repository;

	private IStatus error;

	public UpdateRepositoryConfigurationJob(String name, TaskRepository repository,
			AbstractRepositoryConnector connector) {
		super(name);
		this.connector = connector;
		this.repository = repository;
	}

	@Override
	public IStatus getStatus() {
		return error;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor = SubMonitor.convert(monitor);
		monitor.beginTask(Messages.UpdateRepositoryConfigurationJob_Receiving_configuration, IProgressMonitor.UNKNOWN);
		try {
			try {
				connector.updateRepositoryConfiguration(repository, null, monitor);
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
