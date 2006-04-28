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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import java.io.IOException;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class ContextAttachWizard extends Wizard {

	public static final String WIZARD_TITLE = "Attach context";

	private final TaskRepository repository;

	private final AbstractRepositoryTask task;

	private ContextAttachWizardPage wizardPage;

	public ContextAttachWizard(AbstractRepositoryTask task) {
		repository = MylarTaskListPlugin.getRepositoryManager().getRepository(task.getRepositoryKind(),
				task.getRepositoryUrl());
		this.task = task;
		setWindowTitle(ContextRetrieveWizard.TITLE);
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY_CONTEXT);
	}

	@Override
	public void addPages() {
		wizardPage = new ContextAttachWizardPage(repository, task);
		addPage(wizardPage);
		super.addPages();
	}

	@Override
	public final boolean performFinish() {

		AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
				this.repository.getKind());

		try {
			if (connector.attachContext(repository, task, wizardPage.getComment())) {
				IWorkbenchSite site = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
				if (site instanceof IViewSite) {
					IStatusLineManager statusLineManager = ((IViewSite)site).getActionBars().getStatusLineManager();
					statusLineManager.setMessage(TaskListImages.getImage(TaskListImages.TASKLIST),
							"Context attached to task: " + task.getDescription());					
				}
			} else {
				MessageDialog.openError(null, "Context Attachment",
						"Attachment of task context failed.");
			}
		} catch (IOException e) {
			MessageDialog
					.openError(null, "Context Attachment", "Attachment of task context FAILED.\n" + e.getMessage());
		}

		return true;
	}

}
