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
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.TaskListPreferenceConstants;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @author Rob Elves
 */
public class EditBugzillaQueryWizard extends Wizard {

	private static final String TITLE = "Edit Bugzilla Query";

	private final TaskRepository repository;

	private AbstractBugzillaQueryPage page;

	private BugzillaRepositoryQuery query;

	public EditBugzillaQueryWizard(TaskRepository repository, BugzillaRepositoryQuery query) {
		this.repository = repository;
		this.query = query;
		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE);
	}

	@Override
	public void addPages() {
		if (query.isCustomQuery()) {
			page = new BugzillaCustomQueryWizardPage(repository, query);
		} else {
			page = new BugzillaSearchPage(repository, query);
		}
		addPage(page);

	}

	@Override
	public boolean performFinish() {

		query = page.getQuery();

		final String queryTitle = page.getQueryTitle().trim();

		MylarTaskListPlugin.getTaskListManager().getTaskList().renameContainer(query, queryTitle);

		boolean offline = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(TaskListPreferenceConstants.WORK_OFFLINE);
		if (!offline) {
			WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				protected void execute(IProgressMonitor monitor) throws CoreException {
					monitor.beginTask("Executing query", 50);
					try {
						AbstractRepositoryConnector client = MylarTaskListPlugin.getRepositoryManager()
								.getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);

						client.synchronize(query, null);
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

		return true;
	}

	@Override
	public boolean canFinish() {
		if (page != null && page.isPageComplete()) {
			return true;
		}

		return false;
	}
}
