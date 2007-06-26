/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.planner;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskPlanningEditor;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class TaskActivityWizard extends Wizard implements INewWizard {

	private static final String TITLE = "New Task Activity Report";

	private TaskActivityWizardPage planningGamePage;

	public TaskActivityWizard() {
		super();
		init();
		setWindowTitle(TITLE);
	}

	@Override
	public boolean performFinish() {
		try {
			IWorkbenchPage page = TasksUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page == null)
				return false;
			IEditorInput input = new TaskActivityEditorInput(planningGamePage.getReportStartDate(),
					planningGamePage.getSelectedContainers(), TasksUiPlugin.getTaskListManager().getTaskList());
			page.openEditor(input, TaskPlanningEditor.ID_EDITOR_PLANNING);
		} catch (PartInitException ex) {
			StatusHandler.log(ex, "couldn't open summary editor");
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void init() {
		planningGamePage = new TaskActivityWizardPage();
		super.setForcePreviousAndNextButtons(true);
	}

	@Override
	public void addPages() {
		addPage(planningGamePage);
	}
}
