/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.AbstractErrorReporter;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ReportErrorWizard;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskErrorReporter {

	private final PluginRepositoryMappingManager mappingManager;

	private final TaskContributorManager contributorManager;

	public TaskErrorReporter() {
		this.contributorManager = new TaskContributorManager();
		this.mappingManager = new PluginRepositoryMappingManager();
	}

	public TaskContributorManager getContributorManager() {
		return contributorManager;
	}

	public PluginRepositoryMappingManager getMappingManager() {
		return mappingManager;
	}

	public int getPriority(IStatus status) {
		Assert.isNotNull(status);
		String pluginId = status.getPlugin();
		for (int i = 0; i <= pluginId.length(); i++) {
			if (mappingManager.getMapping(pluginId.substring(0, i)) != null) {
				return AbstractErrorReporter.PRIORITY_DEFAULT;
			}
		}
		return AbstractErrorReporter.PRIORITY_NONE;
	}

	public void process(IStatus status) {
		Assert.isNotNull(status);
		AttributeTaskMapper mapper = preProcess(status);
		postProcess(mapper);
	}

	public AttributeTaskMapper preProcess(IStatus status) {
		Assert.isNotNull(status);
		String pluginId = status.getPlugin();
		Map<String, String> attributes = mappingManager.getAllAttributes(pluginId);
		contributorManager.preProcess(status, attributes);
		return new AttributeTaskMapper(attributes);
	}

	public void postProcess(AttributeTaskMapper mapper) {
		Assert.isNotNull(mapper);
		TaskData taskData;
		try {
			taskData = mapper.createTaskData(null);
			TasksUiInternal.createAndOpenNewTask(taskData);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handle(final IStatus status) {
		ReportErrorWizard wizard = new ReportErrorWizard(TaskErrorReporter.this, status);
		WizardDialog dialog = new WizardDialog(TasksUiInternal.getShell(), wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
	}

	// legacy support
//	TaskRepository taskRepository = mapper.getTaskRepository();
//	if (taskRepository != null) {
//		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
//				taskRepository.getConnectorKind());
//		if (connector instanceof AbstractLegacyRepositoryConnector) {
//			try {
//				if (openLegacyTaskEditor(status, taskRepository, mapper)) {
//					return;
//				}
//			} catch (OperationCanceledException e) {
//				return;
//			}
//		}
//	}
//
//	@Deprecated
//	private boolean openLegacyTaskEditor(IStatus status, TaskRepository taskRepository, AttributeTaskMapper mapper) {
//		RepositoryTaskData taskData = createLegacyTaskData(taskRepository, mapper);
//		if (taskData != null) {
//			taskData.setSummary(status.getMessage());
//
//			TaskContributorManager manager = new TaskContributorManager();
//			manager.updateAttributes(taskData, status);
//
//			String editorId = manager.getEditorId(status);
//
//			NewTaskEditorInput editorInput = new NewTaskEditorInput(taskRepository, taskData);
//			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//			TasksUiUtil.openEditor(editorInput, editorId, page);
//			return true;
//		}
//		return false;
//	}
//
//	@Deprecated
//	private RepositoryTaskData createLegacyTaskData(final TaskRepository taskRepository, AttributeTaskMapper mapper) {
//		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
//				taskRepository.getConnectorKind());
//		if (!(connector instanceof AbstractLegacyRepositoryConnector)) {
//			return null;
//		}
//		final AbstractTaskDataHandler taskDataHandler = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskDataHandler();
//		if (taskDataHandler == null) {
//			return null;
//		}
//
//		AbstractAttributeFactory attributeFactory = taskDataHandler.getAttributeFactory(
//				taskRepository.getRepositoryUrl(), taskRepository.getConnectorKind(), AbstractTask.DEFAULT_TASK_KIND);
//
//		final RepositoryTaskData taskData = new RepositoryTaskData(attributeFactory, taskRepository.getConnectorKind(),
//				taskRepository.getRepositoryUrl(), TasksUiPlugin.getDefault().getNextNewRepositoryTaskId());
//		taskData.setNew(true);
//
//		mapper.applyTo(taskData);
//
//		try {
//			IRunnableWithProgress runnable = new IRunnableWithProgress() {
//				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
//					try {
//						if (!taskDataHandler.initializeTaskData(taskRepository, taskData, monitor)) {
//							throw new InvocationTargetException(new Exception());
//						}
//					} catch (CoreException e) {
//						throw new InvocationTargetException(e);
//					} catch (OperationCanceledException e) {
//						throw new InterruptedException();
//					}
//				}
//			};
//
//			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
//		} catch (InvocationTargetException e) {
//			return null;
//		} catch (InterruptedException e) {
//			throw new OperationCanceledException();
//		}
//
//		taskDataHandler.cloneTaskData(mapper.createTaskSelection().getLegacyTaskData(), taskData);
//
//		return taskData;
//	}

}
