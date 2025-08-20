/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ReportErrorWizard;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.bugs.IProduct;
import org.eclipse.mylyn.tasks.bugs.ITaskContribution;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Steffen Pingel
 */
public class TaskErrorReporter {

	private final SupportHandlerManager handlerManager;

	private final SupportProviderManager providerManager;

	public TaskErrorReporter() {
		handlerManager = new SupportHandlerManager();
		providerManager = new SupportProviderManager();
	}

	public SupportHandlerManager getHandlerManager() {
		return handlerManager;
	}

	public SupportRequest preProcess(IStatus status, IProduct product) {
		Assert.isNotNull(status);
		//Map<String, String> attributes = mappingManager.getAllAttributes(namespace);
		SupportRequest request = new SupportRequest(providerManager, status, product);
		handlerManager.preProcess(request);
		return request;
	}

	public boolean process(final ITaskContribution response, IRunnableContext context) {
		Assert.isNotNull(response);
		ICoreRunnable runner = monitor -> {
			try {
				monitor.beginTask(Messages.TaskErrorReporter_Job_Progress_Process_support_request,
						IProgressMonitor.UNKNOWN);
				process((AttributeTaskMapper) response, monitor);
			} finally {
				monitor.done();
			}
		};
		try {
			CommonUiUtil.run(context, runner);
		} catch (CoreException e) {
			TasksUiInternal.logAndDisplayStatus(Messages.TaskErrorReporter_Create_Task_Error_Title, new Status(
					IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN, Messages.TaskErrorReporter_Create_Task_Error_Message, e));
			return false;
		} catch (OperationCanceledException e) {
			return false;
		}
		return true;
	}

	public void process(AttributeTaskMapper mapper, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(mapper);
		handlerManager.process(mapper, monitor);
		if (!mapper.isHandled()) {
			final TaskData taskData = mapper.createTaskData(monitor);
			mapper.setTaskData(taskData);
			handlerManager.postProcess(mapper, monitor);
			// XXX open task asynchronously to make sure the workbench is active
			Display.getDefault().asyncExec(() -> {
				try {
					TasksUiInternal.createAndOpenNewTask(taskData);
				} catch (CoreException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN,
							"Unexpected error while creating task", e)); //$NON-NLS-1$
				}
			});
		}
	}

	public void handle(final IStatus status) {
		ReportErrorWizard wizard = new ReportErrorWizard(this, status);
		WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
		dialog.setBlockOnOpen(false);
		dialog.setPageSize(500, 200);
		dialog.open();
	}

	public SupportProviderManager getProviderManager() {
		return providerManager;
	}

}
