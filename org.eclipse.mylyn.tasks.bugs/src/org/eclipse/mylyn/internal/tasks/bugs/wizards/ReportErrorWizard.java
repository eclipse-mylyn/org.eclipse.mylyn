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
import org.eclipse.mylyn.internal.tasks.bugs.TaskErrorReporter;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskPage;

/**
 * @author Steffen Pingel
 */
public class ReportErrorWizard extends Wizard {

	private final IStatus status;

	private final AttributeTaskMapper mapper;

	private ReportErrorPage reportErrorPage;

	private NewTaskPage newTaskPage;

	private final TaskErrorReporter taskErrorReporter;

	public ReportErrorWizard(TaskErrorReporter taskErrorReporter, IStatus status) {
		this.taskErrorReporter = taskErrorReporter;
		this.status = status;
		this.mapper = taskErrorReporter.preProcess(status);
		setWindowTitle("Report as Bug");
	}

	@Override
	public void addPages() {
		reportErrorPage = new ReportErrorPage(mapper, status);
		addPage(reportErrorPage);
		newTaskPage = new NewTaskPage(ITaskRepositoryFilter.CAN_CREATE_NEW_TASK, mapper.getTaskMapping());
		addPage(newTaskPage);
	}

	@Override
	public boolean performFinish() {
		if (reportErrorPage.getTaskRepository() != null) {
			taskErrorReporter.postProcess(mapper);
			return true;
		} else {
			return newTaskPage.performFinish();
		}
	}
}
