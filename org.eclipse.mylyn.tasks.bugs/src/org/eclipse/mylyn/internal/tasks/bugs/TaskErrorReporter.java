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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.AbstractErrorReporter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProduct;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.FeatureStatus;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ReportErrorWizard;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskErrorReporter {

	private final PluginRepositoryMappingManager mappingManager;

	private final TaskContributorManager contributorManager;

	private final SupportProviderManager providerManager;

	public TaskErrorReporter() {
		this.contributorManager = new TaskContributorManager();
		this.mappingManager = new PluginRepositoryMappingManager();
		this.providerManager = new SupportProviderManager();
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

//	public void process(IStatus status) {
//		Assert.isNotNull(status);
//		AttributeTaskMapper mapper = preProcess(status);
//		postProcess(mapper);
//	}

	public SupportRequest preProcess(IStatus status, IProduct product) {
		Assert.isNotNull(status);
		//Map<String, String> attributes = mappingManager.getAllAttributes(namespace);
		SupportRequest request = new SupportRequest(providerManager, status, product);
		contributorManager.preProcess(request);
		return request;
	}

	public void postProcess(AttributeTaskMapper mapper) {
		Assert.isNotNull(mapper);
		contributorManager.process(mapper);
		try {
			TaskData taskData = mapper.createTaskData(null);
			mapper.setTaskData(taskData);
			contributorManager.postProcess(mapper);
			TasksUiInternal.createAndOpenNewTask(taskData);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, "Unexpected error reporting error", //$NON-NLS-1$
					e));
		}
	}

	public void handle(final IStatus status) {
		if (status instanceof FeatureStatus) {
			SupportRequest request = preProcess(status, ((FeatureStatus) status).getProduct());
			postProcess((AttributeTaskMapper) request.getDefaultContribution());
		} else {
			ReportErrorWizard wizard = new ReportErrorWizard(TaskErrorReporter.this, status);
			WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
			dialog.setBlockOnOpen(false);
			dialog.setPageSize(500, 200);
			dialog.open();
		}
	}

	public SupportProviderManager getProviderManager() {
		return providerManager;
	}

}
