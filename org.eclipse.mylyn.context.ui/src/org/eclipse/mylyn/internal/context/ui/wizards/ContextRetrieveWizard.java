/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author Rob Elves
 * @author Mik Kersten
 * @author Steffen Pingel
 */
@SuppressWarnings( { "deprecation", "restriction" })
public class ContextRetrieveWizard extends Wizard {

	private static final String TITLE = "Task Repository";

	private final TaskRepository repository;

	private final ITask task;

	private ContextRetrieveWizardPage wizardPage;

	public ContextRetrieveWizard(ITask task) {
		this.task = task;
		this.repository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY_CONTEXT);
	}

	@Override
	public void addPages() {
		wizardPage = new ContextRetrieveWizardPage(repository, task);
		addPage(wizardPage);
		super.addPages();
	}

	@Override
	public final boolean performFinish() {
		RepositoryAttachment attachment = wizardPage.getSelectedContext();
		return AttachmentUtil.downloadContext(task, attachment, getContainer());
	}

}
