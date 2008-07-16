/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.AddExistingTaskJob;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Brock Janiczak
 * @author Mik Kersten
 */
// TODO 3.1 remove class
@Deprecated
public class CommonAddExistingTaskWizard extends Wizard {

	private final TaskRepository repository;

	private ExistingTaskWizardPage page;

	public CommonAddExistingTaskWizard(TaskRepository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(AddExistingTaskWizard.TITLE);
		init();
	}

	@Override
	public final boolean performFinish() {
		final IProgressService svc = PlatformUI.getWorkbench().getProgressService();
		final AddExistingTaskJob job = new AddExistingTaskJob(repository, getTaskId());
		job.schedule();
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {
				svc.showInDialog(getShell(), job);
			}

		});
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	private void init() {
		super.setForcePreviousAndNextButtons(true);
	}

	@Override
	public void addPages() {
		super.addPages();
		this.page = new ExistingTaskWizardPage();
		addPage(page);
	}

	protected String getTaskId() {
		return page.getTaskId();
	}
}
