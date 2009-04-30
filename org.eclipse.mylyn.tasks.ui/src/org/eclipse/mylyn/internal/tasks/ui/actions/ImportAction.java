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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TransferList;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.ImportExportUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Steffen Pingel
 */
public class ImportAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// ignore
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	public void run(IAction action) {
		FileDialog dialog = new FileDialog(WorkbenchUtil.getShell());
		dialog.setText(Messages.ImportAction_Dialog_Title);
		ImportExportUtil.configureFilter(dialog);
		String path = dialog.open();
		if (path != null) {
			File file = new File(path);
			if (file.isFile()) {
				IStatus result = importElements(file);
				if (!result.isOK()) {
					StatusHandler.log(result);
					TasksUiInternal.displayStatus(Messages.ImportAction_Dialog_Title, new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0,
							new IStatus[] { result },
							Messages.ImportAction_Problems_encountered, null));
				}
			}
		}
	}

	private IStatus importElements(File file) {
		MultiStatus result = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0,
				"Problems encounted during importing", null); //$NON-NLS-1$

		TransferList list = new TransferList();
		TaskListExternalizer externalizer = TasksUiPlugin.getDefault().createTaskListExternalizer();
		try {
			externalizer.readTaskList(list, file);
		} catch (CoreException e) {
			result.add(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Problems encountered reading import file", e)); //$NON-NLS-1$
		}

		TaskList taskList = TasksUiPlugin.getTaskList();
		for (AbstractTask task : list.getAllTasks()) {
			if (!validateRepository(task.getConnectorKind(), task.getRepositoryUrl())) {
				result.add(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, NLS.bind(
						"Task {0} ignored, unknown connector", task.getSummary()))); //$NON-NLS-1$
				continue;
			}

			if (taskList.getTask(task.getHandleIdentifier()) != null) {
				result.add(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, NLS.bind(
						"Task {0} ignored, already exists in Task List", task.getSummary()))); //$NON-NLS-1$
			} else {
				// need to deactivate since activation is managed centrally
				task.setActive(false);
				taskList.addTask(task);
				// TODO support importing of contexts
				//ContextCore.getContextStore().importContext(task.getHandleIdentifier(), zipFile);
			}
		}

		for (RepositoryQuery query : list.getQueries()) {
			if (!validateRepository(query.getConnectorKind(), query.getRepositoryUrl())) {
				result.add(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, NLS.bind(
						"Query {0} ignored, unknown connector", query.getSummary()))); //$NON-NLS-1$
				continue;
			}

			if (taskList.getQueries().contains(query)) {
				query.setHandleIdentifier(taskList.getUniqueHandleIdentifier());
			}
			taskList.addQuery(query);
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					query.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQuery(connector, query, null, true);
			}
		}

		return result;
	}

	private boolean validateRepository(String connectorKind, String repositoryUrl) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(connectorKind, repositoryUrl);
		if (repository == null) {
			if (TasksUi.getRepositoryConnector(connectorKind) == null) {
				return false;
			}
			repository = new TaskRepository(connectorKind, repositoryUrl);
			TasksUi.getRepositoryManager().addRepository(repository);
		}
		return true;
	}

}
