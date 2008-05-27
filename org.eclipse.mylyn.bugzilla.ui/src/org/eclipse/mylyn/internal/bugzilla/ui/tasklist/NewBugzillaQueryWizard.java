/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;

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

		BugzillaRepositoryQuery queryCategory = (BugzillaRepositoryQuery) page.getQuery();
		TasksUiInternal.getTaskList().addQuery(queryCategory);
		AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(BugzillaCorePlugin.CONNECTOR_KIND);
		TasksUiInternal.synchronizeQuery(connector, queryCategory, null, true);
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
