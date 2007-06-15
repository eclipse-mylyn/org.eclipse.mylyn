/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.ui.actions.AbstractTaskRepositoryAction;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class ResetRepositoryConfigurationAction extends AbstractTaskRepositoryAction {

	private static final String ID = "org.eclipse.mylyn.tasklist.repositories.reset";

	public ResetRepositoryConfigurationAction() {
		super("Update Attributes");
		setId(ID);
		setEnabled(false);
	}

	@Override
	public void run() {
		try {
			IStructuredSelection selection = getStructuredSelection();
			for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
				Object selectedObject = iter.next();
				if (selectedObject instanceof TaskRepository) {
					final TaskRepository repository = (TaskRepository) selectedObject;
					final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
					if (connector != null) {
						final String jobName = "Updating attributes for: " + repository.getUrl();
						Job updateJob = new Job(jobName) {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								monitor.beginTask(jobName,
										IProgressMonitor.UNKNOWN);
								try {
									connector.updateAttributes(repository, monitor);
								} catch (CoreException ce) {
									StatusManager.displayStatus("Error updating attributes", ce.getStatus());
								}
								
								monitor.done();	
								return Status.OK_STATUS;
							}
							
						};
						updateJob.schedule();
					}
				}
			}
		} catch (Exception e) {
			StatusManager.fail(e, e.getMessage(), true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
