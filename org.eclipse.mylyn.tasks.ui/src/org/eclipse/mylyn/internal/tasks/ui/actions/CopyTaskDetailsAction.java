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
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
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

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.copy"; //$NON-NLS-1$

	private final Clipboard clipboard;

	private static String lineSeparator = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	public CopyTaskDetailsAction() {
		super(Messages.CopyTaskDetailsAction_Copy_Details);
		setToolTipText(Messages.CopyTaskDetailsAction_Copy_Details);
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
				sb.append(lineSeparator);
				sb.append(lineSeparator);
			}
			sb.append(getTextForTask(seletedElements[i]));
		}
		if (sb.length() > 0) {
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] { sb.toString() }, new Transfer[] { textTransfer });
		}
	}

	// TODO move to TasksUiUtil / into core
	public static String getTextForTask(Object object) {
		StringBuffer sb = new StringBuffer();
		if (object instanceof ITask) {
			AbstractTask task = (AbstractTask) object;
			if (task.getTaskKey() != null) {
				sb.append(task.getTaskKey());
				sb.append(": "); //$NON-NLS-1$
			}

			sb.append(task.getSummary());
			if (TasksUiInternal.isValidUrl(task.getUrl())) {
				sb.append(lineSeparator);
				sb.append(task.getUrl());
			}
		} else if (object instanceof IRepositoryQuery) {
			RepositoryQuery query = (RepositoryQuery) object;
			sb.append(query.getSummary());
			if (TasksUiInternal.isValidUrl(query.getUrl())) {
				sb.append(lineSeparator);
				sb.append(query.getUrl());
			}
		} else if (object instanceof IRepositoryElement) {
			IRepositoryElement element = (IRepositoryElement) object;
			sb.append(element.getSummary());
		}
		return sb.toString();
	}

}
