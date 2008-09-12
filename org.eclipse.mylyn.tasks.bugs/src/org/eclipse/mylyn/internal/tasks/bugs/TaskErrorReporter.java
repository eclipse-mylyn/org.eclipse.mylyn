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

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.AbstractErrorReporter;
import org.eclipse.mylyn.commons.core.StatusHandler;
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
			StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Unexpected error reporting error",
					e));
		}
	}

	public void handle(final IStatus status) {
		ReportErrorWizard wizard = new ReportErrorWizard(TaskErrorReporter.this, status);
		WizardDialog dialog = new WizardDialog(TasksUiInternal.getShell(), wizard);
		dialog.setBlockOnOpen(false);
		dialog.setPageSize(500, 200);
		dialog.open();
	}

}
