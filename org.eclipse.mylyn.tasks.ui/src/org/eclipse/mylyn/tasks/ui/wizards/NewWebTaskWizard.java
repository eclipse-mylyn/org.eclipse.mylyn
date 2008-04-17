/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewWebTaskPage;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard for creating new tickets through a web browser.
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 2.0
 */
public class NewWebTaskWizard extends Wizard implements INewWizard {

	protected TaskRepository taskRepository;

	protected String newTaskUrl;

	private final TaskSelection taskSelection;

	/**
	 * @since 3.0
	 */
	public NewWebTaskWizard(TaskRepository taskRepository, String newTaskUrl, TaskSelection taskSelection) {
		this.taskRepository = taskRepository;
		this.newTaskUrl = newTaskUrl;
		this.taskSelection = taskSelection;

		setWindowTitle("New Task");
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(new NewWebTaskPage(taskSelection));
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	@Override
	public boolean performFinish() {
		handleSelection(taskSelection);
		TasksUiUtil.openUrl(newTaskUrl);
		return true;
	}

	private void handleSelection(final TaskSelection taskSelection) {
		if (taskSelection == null) {
			return;
		}

		RepositoryTaskData taskData = taskSelection.getTaskData();
		String summary = taskData.getSummary();
		String description = taskData.getDescription();

		Clipboard clipboard = new Clipboard(getShell().getDisplay());
		clipboard.setContents(new Object[] { summary + "\n" + description },
				new Transfer[] { TextTransfer.getInstance() });

		MessageDialog.openInformation(
				getShell(),
				ITasksUiConstants.TITLE_DIALOG,
				"This connector does not provide a rich task editor for creating tasks.\n\n"
						+ "The error contents have been placed in the clipboard so that you can paste them into the entry form.");
	}

}
