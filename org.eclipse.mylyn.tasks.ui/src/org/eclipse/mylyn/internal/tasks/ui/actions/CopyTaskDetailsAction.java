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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
		setImageDescriptor(CommonImages.COPY);
		// FIXME the clipboard is not disposed
		Display display = PlatformUI.getWorkbench().getDisplay();
		clipboard = new Clipboard(display);
	}

	@Override
	public void run() {
		ISelection selection = getStructuredSelection();
		StringBuilder sb = new StringBuilder();
		Object[] seletedElements = ((IStructuredSelection) selection).toArray();
		for (int i = 0; i < seletedElements.length; i++) {
			if (i > 0) {
				sb.append("\n\n");
			}
			sb.append(getTextForTask(seletedElements[i]));
		}
		if (sb.length() > 0) {
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] { sb.toString() }, new Transfer[] { textTransfer });
		}
	}

	// API 3.0: move to TasksUiUtil / into core
	public static String getTextForTask(Object object) {
		String text = "";
		if (object instanceof ITask) {
			AbstractTask task = (AbstractTask) object;
			if (task.getTaskKey() != null) {
				text += task.getTaskKey() + ": ";
			}

			text += task.getSummary();
			if (TasksUiInternal.isValidUrl(task.getUrl())) {
				text += "\n" + task.getUrl();
			}
		} else if (object instanceof RepositoryTaskData) {
			RepositoryTaskData taskData = (RepositoryTaskData) object;
			if (taskData.getTaskKey() != null) {
				text += taskData.getTaskKey() + ": ";
			}

			text += taskData.getSummary();
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					taskData.getConnectorKind());
			if (connector != null) {
				text += "\n" + connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getTaskId());
			}
		} else if (object instanceof IRepositoryQuery) {
			RepositoryQuery query = (RepositoryQuery) object;
			text += query.getSummary();
			text += "\n" + query.getUrl();
		} else if (object instanceof IRepositoryElement) {
			IRepositoryElement element = (IRepositoryElement) object;
			text = element.getSummary();
		} else if (object instanceof RepositoryTaskSelection) {
			RepositoryTaskSelection selection = (RepositoryTaskSelection) object;
			text += selection.getId() + ": " + selection.getBugSummary();
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
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
