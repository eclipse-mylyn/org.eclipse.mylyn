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

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.bugs.AttributeTaskMapper;
import org.eclipse.mylyn.internal.tasks.bugs.KeyValueMapping;
import org.eclipse.mylyn.internal.tasks.bugs.SupportRequest;
import org.eclipse.mylyn.internal.tasks.bugs.TaskErrorReporter;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskPage;

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
		this.request = taskErrorReporter.preProcess(status);
		setWindowTitle(Messages.ReportErrorWizard_Report_as_Bug);
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
	public boolean performFinish() {
		if (reportErrorPage.getSelectedContribution() != null) {
			taskErrorReporter.postProcess(reportErrorPage.getSelectedContribution());
			return true;
		} else {
			return newTaskPage.performFinish();
		}
	}

}
