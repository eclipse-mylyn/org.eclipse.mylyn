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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Rob Elves
 */
public class ContextAttachWizard extends Wizard {

	public static final String WIZARD_TITLE = "Attach Context";	
	private final TaskRepository repository;
	private final AbstractRepositoryTask task;
	private ContextAttachWizardPage wizardPage;

	public ContextAttachWizard(AbstractRepositoryTask task) {
		repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
				task.getRepositoryKind(), task.getRepositoryUrl());		
		this.task = task;
//		init();
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
				
				MessageDialog.openInformation(null, "Context Attachment",
						"Attachment of task context was successful.\n Attached to: " + task.getDescription());
			} else {
				MessageDialog.openError(null, "Context Attachment", "Attachment of task context FAILED. See error log for details.");
			}
		} catch (IOException e) {
			MessageDialog.openError(null, "Context Attachment", "Attachment of task context FAILED.\n"+e.getMessage());
		}

		return true;
	}

//	public void init(IWorkbench workbench, IStructuredSelection selection) {
//	}

//	private void init() {
////		super.setForcePreviousAndNextButtons(true);
//	}

}
