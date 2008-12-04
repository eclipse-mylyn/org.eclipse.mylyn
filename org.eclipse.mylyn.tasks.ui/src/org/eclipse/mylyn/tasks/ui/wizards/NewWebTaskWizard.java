/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewWebTaskPage;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
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

	private final ITaskMapping taskSelection;

	/**
	 * @since 3.0
	 */
	public NewWebTaskWizard(TaskRepository taskRepository, String newTaskUrl, ITaskMapping taskSelection) {
		this.taskRepository = taskRepository;
		this.newTaskUrl = newTaskUrl;
		this.taskSelection = taskSelection;

		setWindowTitle(Messages.NewWebTaskWizard_New_Task);
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

	private void handleSelection(final ITaskMapping taskSelection) {
		if (taskSelection == null) {
			return;
		}

		String summary = taskSelection.getSummary();
		String description = taskSelection.getDescription();

		Clipboard clipboard = new Clipboard(getShell().getDisplay());
		clipboard.setContents(new Object[] { summary + "\n" + description }, //$NON-NLS-1$
				new Transfer[] { TextTransfer.getInstance() });

		MessageDialog.openInformation(getShell(), Messages.NewWebTaskWizard_New_Task,
				Messages.NewWebTaskWizard_This_connector_does_not_provide_a_rich_task_editor_for_creating_tasks);
	}

}
