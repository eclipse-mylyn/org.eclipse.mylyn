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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylar.internal.bugzilla.ui.wizard.NewBugzillaReportWizard;
import org.eclipse.mylar.internal.tasks.ui.search.AbstractRepositoryQueryPage;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class BugzillaRepositoryUi extends AbstractRepositoryConnectorUi {

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new BugzillaRepositorySettingsPage(this);
	}

	@Override
	public AbstractRepositoryQueryPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new BugzillaSearchPage(repository);
	}

	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		if (!(query instanceof BugzillaRepositoryQuery)) {
			return;
		}

		try {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
					query.getRepositoryUrl());
			if (repository == null)
				return;

			IWizard wizard = this.getEditQueryWizard(repository, query);

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Bugzilla Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Dialog.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		return new NewBugzillaReportWizard(taskRepository, selection);
	}

	public IWizard getEditQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (!(query instanceof BugzillaRepositoryQuery)) {
			return null;
		}
		return new EditBugzillaQueryWizard(repository, (BugzillaRepositoryQuery) query);
	}

	public IWizard getNewQueryWizard(TaskRepository repository, IStructuredSelection selection) {
		return new NewBugzillaQueryWizard(repository);
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public boolean hasRichEditor() {
		return true;
	}

	@Override
	public String getRepositoryType() {
		return BugzillaCorePlugin.REPOSITORY_KIND;
	}

	@Override
	public void openRemoteTask(String repositoryUrl, String idString) {
		int id = -1;
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
			// ignore
		}
		if (id != -1) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			OpenBugzillaReportJob job = new OpenBugzillaReportJob(repositoryUrl, id, page);
			job.schedule();
		}
	}

}
