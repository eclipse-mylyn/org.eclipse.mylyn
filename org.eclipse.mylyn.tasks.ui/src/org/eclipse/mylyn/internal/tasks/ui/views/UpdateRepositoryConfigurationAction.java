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

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AbstractTaskRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class UpdateRepositoryConfigurationAction extends AbstractTaskRepositoryAction {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.reset"; //$NON-NLS-1$

	public UpdateRepositoryConfigurationAction() {
		super(Messages.UpdateRepositoryConfigurationAction_Update_Repository_Configuration);
		setId(ID);
		setEnabled(false);
	}

	@Override
	public void run() {
		try {
			IStructuredSelection selection = getStructuredSelection();
			for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
				final TaskRepository repository = getTaskRepository(iter.next());
				if (repository != null) {
					final AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
							.getRepositoryConnector(repository.getConnectorKind());
					if (connector != null) {
						final String jobName = MessageFormat.format(
								Messages.UpdateRepositoryConfigurationAction_Updating_repository_configuration_for_X,
								repository.getRepositoryUrl());
						Job updateJob = new Job(jobName) {
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
								try {
									performUpdate(repository, connector, monitor);
								} finally {
									monitor.done();
								}
								return Status.OK_STATUS;
							}
						};
						updateJob.schedule();
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
		}
	}

	public void performUpdate(final TaskRepository repository, final AbstractRepositoryConnector connector,
			IProgressMonitor monitor) {
		try {
			connector.updateRepositoryConfiguration(repository, monitor);
		} catch (final CoreException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					TasksUiInternal.displayStatus(
							Messages.UpdateRepositoryConfigurationAction_Error_updating_repository_configuration,
							e.getStatus());
				}
			});
		}
	}

}
