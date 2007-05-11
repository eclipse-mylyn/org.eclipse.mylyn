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
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.search.AbstractRepositoryQueryPage;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @author Mik Kersten
 * @author Brock Janiczak
 */
public class NewBugzillaQueryWizard extends Wizard {

	private static final String TITLE = "New Bugzilla Query";

	private final TaskRepository repository;

	BugzillaQueryTypeWizardPage page1;

	public NewBugzillaQueryWizard(TaskRepository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE);
	}

	@Override
	public void addPages() {
		page1 = new BugzillaQueryTypeWizardPage(repository);
		page1.setWizard(this);
		addPage(page1);

	}

	@Override
	public boolean performFinish() {

		AbstractRepositoryQueryPage page;

		if (page1.getNextPage() != null && page1.getNextPage() instanceof AbstractRepositoryQueryPage) {
			page = (AbstractRepositoryQueryPage) page1.getNextPage();
		} else {
			return false;
		}

		final BugzillaRepositoryQuery queryCategory = (BugzillaRepositoryQuery) page.getQuery();

		TasksUiPlugin.getTaskListManager().getTaskList().addQuery(queryCategory);
//		boolean offline = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
//		if (!offline) {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException {
					monitor.beginTask("Executing query", 50);
					try {
						AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
								.getRepositoryConnector(BugzillaCorePlugin.REPOSITORY_KIND);
						TasksUiPlugin.getSynchronizationManager().synchronize(connector, queryCategory, null, true);
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
//		}

		return true;
	}

	@Override
	public boolean canFinish() {
		if (page1.getNextPage() != null && page1.getNextPage().isPageComplete()) {
			return true;
		}
		return false;
	}

}
