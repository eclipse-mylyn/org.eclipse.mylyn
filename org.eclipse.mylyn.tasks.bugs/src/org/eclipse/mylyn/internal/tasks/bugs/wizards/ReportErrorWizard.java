/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.ITaskRepositoryFilter;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskPage;
import org.eclipse.mylyn.tasks.core.ITaskMapping;

/**
 * @author Steffen Pingel
 */
public class ReportErrorWizard extends Wizard {

	private final IStatus status;

	private final ITaskMapping taskMapping;

	private ReportErrorPage reportErrorPage;

	private NewTaskPage newTaskPage;

	public ReportErrorWizard(IStatus status, ITaskMapping taskMapping) {
		this.status = status;
		this.taskMapping = taskMapping;
		setWindowTitle("Report Error");
	}

	@SuppressWarnings("restriction")
	@Override
	public void addPages() {
		reportErrorPage = new ReportErrorPage(status);
		addPage(reportErrorPage);
		newTaskPage = new NewTaskPage(ITaskRepositoryFilter.CAN_CREATE_NEW_TASK, taskMapping);
		addPage(reportErrorPage);
	}

	@Override
	public boolean performFinish() {
		// ignore
		return false;
	}

}
