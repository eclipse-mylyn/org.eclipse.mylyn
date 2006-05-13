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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IRemoteContextDelegate;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ContextRetrieveWizard extends Wizard {

	public static final String TITLE = "Task Repository";
	 
	public static final String WIZARD_TITLE = "Retrieve context";
	
	private final TaskRepository repository;

	private final AbstractRepositoryTask task;

	private ContextRetrieveWizardPage wizardPage;

	public ContextRetrieveWizard(AbstractRepositoryTask task) {
		repository = MylarTaskListPlugin.getRepositoryManager().getRepository(task.getRepositoryKind(),
				task.getRepositoryUrl());
		this.task = task;
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY_CONTEXT);
	}

	@Override
	public void addPages() {
		wizardPage = new ContextRetrieveWizardPage(repository, task);
		addPage(wizardPage);
		super.addPages();
	}

	@Override
	public final boolean performFinish() {

		IRemoteContextDelegate delegate = wizardPage.getSelectedContext();
		AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
				this.repository.getKind());
		try {
			if (connector.retrieveContext(repository, task, delegate)) {
//				IWorkbenchSite site = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
//				if (site instanceof IViewSite) {
//					IStatusLineManager statusLineManager = ((IViewSite)site).getActionBars().getStatusLineManager();
//					statusLineManager.setMessage(TaskListImages.getImage(TaskListImages.TASKLIST),
//							"Context retrieved for: " + task.getDescription());					
//				} else {
//					MylarStatusHandler.log("could not get part", this);
//				}
			} else {
				MessageDialog.openError(null, "Context Retrieval",
						"Retrieval of task context FAILED. See error log for details.");
			}
		} catch (Exception e) {
			MessageDialog.openError(null, MylarTaskListPlugin.TITLE_DIALOG, "Retrieval of task context FAILED.\n" + e.getMessage());
		}
		return true;
	}

}
