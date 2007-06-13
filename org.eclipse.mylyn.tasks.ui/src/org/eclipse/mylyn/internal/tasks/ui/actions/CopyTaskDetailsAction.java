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
package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.swt.SWT;
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

	private Clipboard clipboard;

	public CopyTaskDetailsAction(boolean setAccelerator) {
		super(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(TasksUiImages.COPY);
		if (setAccelerator) {
			setAccelerator(SWT.MOD1 + 'c');
		}

		Display display = PlatformUI.getWorkbench().getDisplay();
		clipboard = new Clipboard(display);
	}

	@Override
	public void run() {
		ISelection selection = super.getStructuredSelection();
		Object object = ((IStructuredSelection) selection).getFirstElement();
		String text = getTextForTask(object);

		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { text }, new Transfer[] { textTransfer });
	}

	public static String getTextForTask(Object object) {
		String text = "";
		if (object instanceof AbstractTask) {
			AbstractTask task = null;
			if (object instanceof AbstractTask) {
				task = (AbstractTask) object;
			}
			if (task != null) {
				if (task instanceof AbstractTask) {
					text += ((AbstractTask) task).getTaskKey() + ": ";
				}

				text += task.getSummary();
				if (task.hasValidUrl()) {
					text += "\n" + task.getTaskUrl();
				}
			}
		} else if (object instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) object;
			text += query.getSummary();
			text += "\n" + query.getUrl();
		} else if (object instanceof AbstractTaskListElement) {
			AbstractTaskListElement element = (AbstractTaskListElement) object;
			text = element.getSummary();
		} else if (object instanceof RepositoryTaskSelection) {

			RepositoryTaskSelection selection = (RepositoryTaskSelection) object;
			text += selection.getId() + ": " + selection.getBugSummary();
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					selection.getRepositoryKind());
			if (connector != null) {
				text += "\n" + connector.getTaskWebUrl(selection.getRepositoryUrl(), selection.getId());
			} else {
				text += "\n" + selection.getRepositoryUrl();
			}
		}
		return text;
	}
}
