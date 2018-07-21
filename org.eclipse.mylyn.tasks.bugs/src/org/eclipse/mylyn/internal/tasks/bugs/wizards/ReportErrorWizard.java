/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.bugs.AttributeTaskMapper;
import org.eclipse.mylyn.internal.tasks.bugs.KeyValueMapping;
import org.eclipse.mylyn.internal.tasks.bugs.SupportRequest;
import org.eclipse.mylyn.internal.tasks.bugs.TaskErrorReporter;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskPage;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author Steffen Pingel
 */
public class ReportErrorWizard extends Wizard {

	private final IStatus status;

	private final SupportRequest request;

	private ReportErrorPage reportErrorPage;

	private NewTaskPage newTaskPage;

	private final TaskErrorReporter taskErrorReporter;

	public ReportErrorWizard(TaskErrorReporter taskErrorReporter, IStatus status) {
		this.taskErrorReporter = taskErrorReporter;
		this.status = status;
		this.request = taskErrorReporter.preProcess(status, null);
		setWindowTitle(Messages.ReportErrorWizard_Report_as_Bug);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPORT_BUG);
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		reportErrorPage = new ReportErrorPage(request, status);
		addPage(reportErrorPage);
		KeyValueMapping defaultMapping = new KeyValueMapping(
				((AttributeTaskMapper) request.getDefaultContribution()).getAttributes());
		newTaskPage = new NewTaskPage(ITaskRepositoryFilter.CAN_CREATE_NEW_TASK, defaultMapping);
		addPage(newTaskPage);
	}

	@Override
	public boolean canFinish() {
		// newTaskPage is a selection page, therefore it's only valid to finish early if the selected node does not delegate to another wizard
		return reportErrorPage.getSelectedContribution() != null || newTaskPage.canFinish()
				&& newTaskPage.getNextPage() == null;
	}

	@Override
	public boolean performFinish() {
		if (reportErrorPage.getSelectedContribution() != null) {
			return taskErrorReporter.process(reportErrorPage.getSelectedContribution(), getContainer());
		} else {
			return newTaskPage.performFinish();
		}
	}

}
