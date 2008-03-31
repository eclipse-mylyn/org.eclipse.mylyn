/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class CopyTaskDetailsAction extends BaseSelectionListenerAction {

	private static final String LABEL = "Copy Details";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.copy";

	private final Clipboard clipboard;

	public CopyTaskDetailsAction() {
		super(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(TasksUiImages.COPY);

		Display display = PlatformUI.getWorkbench().getDisplay();
		clipboard = new Clipboard(display);
	}

	@Override
	public void run() {
		ISelection selection = super.getStructuredSelection();
		Object object = ((IStructuredSelection) selection).getFirstElement();
		String text = getTextForTask(object);

		TextTransfer textTransfer = TextTransfer.getInstance();
		if (text != null && !text.equals("")) {
			clipboard.setContents(new Object[] { text }, new Transfer[] { textTransfer });
		}
	}

	// API 3.0: move to TasksUiUtil / into core
	public static String getTextForTask(Object object) {
		String text = "";
		if (object instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) object;
			if (task.getTaskKey() != null) {
				text += task.getTaskKey() + ": ";
			}

			text += task.getSummary();
			if (task.hasValidUrl()) {
				text += "\n" + task.getUrl();
			}
		} else if (object instanceof RepositoryTaskData) {
			RepositoryTaskData taskData = (RepositoryTaskData) object;
			if (taskData.getTaskKey() != null) {
				text += taskData.getTaskKey() + ": ";
			}

			text += taskData.getSummary();
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					taskData.getConnectorKind());
			if (connector != null) {
				text += "\n" + connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getId());
			}
		} else if (object instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) object;
			text += query.getSummary();
			text += "\n" + query.getUrl();
		} else if (object instanceof AbstractTaskContainer) {
			AbstractTaskContainer element = (AbstractTaskContainer) object;
			text = element.getSummary();
		} else if (object instanceof RepositoryTaskSelection) {
			RepositoryTaskSelection selection = (RepositoryTaskSelection) object;
			text += selection.getId() + ": " + selection.getBugSummary();
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					selection.getRepositoryKind());
			if (connector != null) {
				text += "\n" + connector.getTaskUrl(selection.getRepositoryUrl(), selection.getId());
			} else {
				text += "\n" + selection.getRepositoryUrl();
			}
		}
		return text;
	}
}
