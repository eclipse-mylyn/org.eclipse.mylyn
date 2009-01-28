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

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.util.ClipboardCopier;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class CopyTaskDetailsAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.copy"; //$NON-NLS-1$

	private final ClipboardCopier copier;

	public CopyTaskDetailsAction() {
		super(Messages.CopyTaskDetailsAction_Copy_Details);
		setToolTipText(Messages.CopyTaskDetailsAction_Copy_Details);
		setId(ID);
		setImageDescriptor(CommonImages.COPY);
		this.copier = new ClipboardCopier() {
			@Override
			protected String getTextForElement(Object element) {
				return getTextForTask(element);
			}
		};
	}

	@Override
	public void run() {
		copier.copy(getStructuredSelection());
	}

	public void dispose() {
		copier.dispose();
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
				sb.append(ClipboardCopier.LINE_SEPARATOR);
				sb.append(task.getUrl());
			}
		} else if (object instanceof IRepositoryQuery) {
			RepositoryQuery query = (RepositoryQuery) object;
			sb.append(query.getSummary());
			if (TasksUiInternal.isValidUrl(query.getUrl())) {
				sb.append(ClipboardCopier.LINE_SEPARATOR);
				sb.append(query.getUrl());
			}
		} else if (object instanceof IRepositoryElement) {
			IRepositoryElement element = (IRepositoryElement) object;
			sb.append(element.getSummary());
		}
		return sb.toString();
	}

}
