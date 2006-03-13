/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @author	Mik Kersten
 * @author 	Brock Janiczak
 */
public class NewBugzillaQueryWizard extends Wizard {

	private final TaskRepository repository;

	private BugzillaQueryWizardPage queryPage;

	public NewBugzillaQueryWizard(TaskRepository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		queryPage = new BugzillaQueryWizardPage(repository);
		addPage(queryPage);
	}

	@Override
	public boolean performFinish() {
		queryPage.getQueryDialog().okPressed();
		final BugzillaRepositoryQuery queryCategory = new BugzillaRepositoryQuery(
				repository.getUrl(), 
				queryPage.getQueryDialog().getUrl(), 
				queryPage.getQueryDialog().getName(), 
				queryPage.getQueryDialog().getMaxHits(), 
				MylarTaskListPlugin.getTaskListManager().getTaskList());
		if (queryPage.getQueryDialog().isCustom()) {
			queryCategory.setCustomQuery(true);
		} 
//		else {
//			queryCategory = new BugzillaCustomRepositoryQuery(
//					repository.getUrl().toExternalForm(), 
//					queryPage.getQueryDialog().getName(), 
//					queryPage.getQueryDialog().getUrl(), 
//					queryPage.getQueryDialog().getMaxHits());
//		}
		MylarTaskListPlugin.getTaskListManager().getTaskList().addQuery(queryCategory);
		boolean offline = MylarTaskListPlugin.getMylarPrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		if (!offline) {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) throws CoreException {
					monitor.beginTask("Executing query", 50);
					try {
						AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
						client.synchronize(queryCategory, null);
//						if (TaskListView.getDefault() != null) {
//							TaskListView.getDefault().getViewer().refresh();
//						}
//						queryCategory.refreshBugs(new SubProgressMonitor(monitor, 50));
					} finally {
						monitor.done();
					}
				}
			};

			try {
				getContainer().run(true, false, op);
			} catch (Exception e) {
				MylarStatusHandler.log(e, "There was a problem executing the query refresh");
			}
		}
//		if (TaskListView.getDefault() != null) {
//			TaskListView.getDefault().getViewer().refresh();
//		}

		return true;
	}

}
