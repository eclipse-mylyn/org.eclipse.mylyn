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
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasklist.TaskRepository;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
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
				repository.getUrl().toExternalForm(), 
				queryPage.getQueryDialog().getUrl(), 
				queryPage.getQueryDialog().getName(), 
				queryPage.getQueryDialog().getMaxHits());
		if (!queryPage.getQueryDialog().isCustom()) {
			queryCategory.setCustomQuery(true);
		} 
//		else {
//			queryCategory = new BugzillaCustomRepositoryQuery(
//					repository.getUrl().toExternalForm(), 
//					queryPage.getQueryDialog().getName(), 
//					queryPage.getQueryDialog().getUrl(), 
//					queryPage.getQueryDialog().getMaxHits());
//		}
		MylarTaskListPlugin.getTaskListManager().addQuery(queryCategory);
		boolean offline = MylarTaskListPlugin.getPrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		if (!offline) {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) throws CoreException {
					monitor.beginTask("Executing query", 50);
					try {
						AbstractRepositoryClient client = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
						client.synchronize(queryCategory);
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
		if (TaskListView.getDefault() != null) {
			// TODO: remove
			TaskListView.getDefault().getViewer().refresh();
		}

		return true;
	}

}
