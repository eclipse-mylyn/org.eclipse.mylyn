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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.core.BugzillaRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractNewQueryPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.ITaskRepositoryClient;
import org.eclipse.mylar.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskRepositoryClient implements ITaskRepositoryClient {

	public String getLabel() {
		return "Bugzilla (supports uncustomized 2.16-2.20)";
	}

	public String toString() {
		return getLabel();
	}

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new BugzillaRepositorySettingsPage();
	}

	public String getKind() {
		return BugzillaPlugin.REPOSITORY_KIND;
	}

	public ITask createTaskFromExistingId(TaskRepository repository, String id) {
		int bugId = -1;
		try {
			if (id != null) {
				bugId = Integer.parseInt(id);
			} else {
				return null;
			}
		} catch (NumberFormatException nfe) {
			TaskListView.getDefault().showMessage("Invalid report id.");
			return null;
		}

		ITask newTask = new BugzillaTask(TaskRepositoryManager.getHandle(repository.getUrl().toExternalForm(), bugId),
				"<bugzilla info>", true, true);
		
		ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(newTask);
		if (taskHandler != null) {
			ITask addedTask = taskHandler.addTaskToRegistry(newTask);
			if (addedTask instanceof BugzillaTask) {
				BugzillaTask newTask2 = (BugzillaTask) addedTask;
				if (newTask2 == newTask) {
					((BugzillaTask) newTask).scheduleDownloadReport();
				} else {
					newTask = newTask2;
					((BugzillaTask) newTask).updateTaskDetails();
				}
			}
		} else {
			((BugzillaTask) newTask).scheduleDownloadReport();
		}
//		BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask) newTask);
		return newTask;
	}

	public AbstractNewQueryPage getQueryPage(TaskRepository repository) {
		return new BugzillaQueryWizardPage(repository);
	}
}
