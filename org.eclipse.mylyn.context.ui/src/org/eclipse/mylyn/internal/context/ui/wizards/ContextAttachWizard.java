/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.context.ui.ContextUiUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class ContextAttachWizard extends Wizard {

	private static final String TITLE = "Task Repository";

	private final TaskRepository repository;

	private final AbstractTask task;

	private ContextAttachWizardPage wizardPage;

	public ContextAttachWizard(AbstractTask task) {
		this.repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		this.task = task;
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY_CONTEXT);
	}

	@Override
	public void addPages() {
		wizardPage = new ContextAttachWizardPage(repository, task);
		addPage(wizardPage);
		super.addPages();
	}

	@Override
	public final boolean performFinish() {
		return ContextUiUtil.uploadContext(repository, task, wizardPage.getComment(), getContainer());
	}

}
