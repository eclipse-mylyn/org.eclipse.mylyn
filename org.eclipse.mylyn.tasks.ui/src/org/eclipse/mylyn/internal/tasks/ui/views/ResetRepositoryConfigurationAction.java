/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.views;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class ResetRepositoryConfigurationAction extends Action {

	private static final String ID = "org.eclipse.mylar.tasklist.repositories.reset";

	private TaskRepositoriesView repositoriesView;

	public ResetRepositoryConfigurationAction(TaskRepositoriesView repositoriesView) {
		this.repositoriesView = repositoriesView;
		setText("Update Attributes");
		setId(ID);
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void run() {
		try {
			IStructuredSelection selection = (IStructuredSelection) repositoriesView.getViewer().getSelection();
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
									MylarStatusHandler.fail(ce, ce.getStatus().getMessage(), true);
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
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
