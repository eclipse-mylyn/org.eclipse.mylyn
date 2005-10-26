/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.planner.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Ken Sueda
 */
public class MylarTaskPlannerWizard extends Wizard implements INewWizard {

	private MylarTaskPlannerWizardPage planningGamePage = null;
	public MylarTaskPlannerWizard() {
		super();
		init();
	}
	
	@Override
	public boolean performFinish() {
		try {
			int numDays = planningGamePage.getNumDays();
			IWorkbenchPage page = MylarTasklistPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page == null)
				return false;
			IEditorInput input = new CompletedTasksEditorInput(numDays,
					MylarTasklistPlugin.getTaskListManager().getTaskList());
			page.openEditor(input, MylarTasklistPlugin.PLANNING_GAME_EDITOR_ID);
		} catch (PartInitException ex) {
			MylarPlugin.log(ex, "couldn't open summary editor");
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void init() {
		planningGamePage = new MylarTaskPlannerWizardPage();
		super.setForcePreviousAndNextButtons(true);
	}
	
	 @Override
	public void addPages() {
		addPage(planningGamePage);
	}
}
