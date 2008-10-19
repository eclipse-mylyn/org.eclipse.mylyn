/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.context.ui.commands.RetrieveContextAttachmentHandler;
import org.eclipse.mylyn.internal.context.ui.commands.RetrieveContextHandler;
import org.eclipse.mylyn.internal.context.ui.wizards.ContextRetrieveWizard;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 * @deprecated use {@link RetrieveContextHandler} or {@link RetrieveContextAttachmentHandler} instead
 */
@SuppressWarnings("restriction")
@Deprecated
public class ContextRetrieveAction extends Action implements IViewActionDelegate {

	private AbstractTask task;

	private static final String ID_ACTION = "org.eclipse.mylyn.context.ui.repository.task.retrieve";

	public ContextRetrieveAction() {
		setText("Retrieve...");
		setToolTipText("Retrieve Task Context");
		setId(ID_ACTION);
		setImageDescriptor(TasksUiImages.CONTEXT_RETRIEVE);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		run(this);
	}

	public void run(IAction action) {
		if (task != null) {
			run(task);
		}
	}

	public void run(ITask task) {
		ContextRetrieveWizard wizard = new ContextRetrieveWizard(task);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.setBlockOnOpen(true);
			if (dialog.open() == Window.CANCEL) {
				dialog.close();
				return;
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		AbstractTask selectedTask = TaskListView.getSelectedTask(selection);
		if (selectedTask != null) {
			task = selectedTask;
			action.setEnabled(AttachmentUtil.canDownloadAttachment(task) && AttachmentUtil.hasContextAttachment(task));
		} else {
			action.setEnabled(false);
		}
	}
}
