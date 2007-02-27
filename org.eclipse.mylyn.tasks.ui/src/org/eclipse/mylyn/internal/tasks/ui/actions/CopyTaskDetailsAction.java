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
package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class CopyTaskDetailsAction extends BaseSelectionListenerAction {

	private static final String LABEL = "Copy Details";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.copy";

	public CopyTaskDetailsAction(boolean setAccelerator) {
		super(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(TaskListImages.COPY);
		if (setAccelerator) {
			setAccelerator(SWT.MOD1 + 'c');
		}
	}

	@Override
	public void run() {
		ISelection selection = super.getStructuredSelection();
		Object object = ((IStructuredSelection) selection).getFirstElement();
		String text = getTextForTask(object); 

		// HACK: this should be done using proper copying
		Composite dummyComposite = TaskListView.getFromActivePerspective().getFilteredTree();
		StyledText styledText = new StyledText(dummyComposite, SWT.NULL);
		styledText.setText(text);
		styledText.selectAll();
		styledText.copy();
		styledText.dispose();
	}

	public static String getTextForTask(Object object) {
		String text = "";
		if (object instanceof ITask || object instanceof AbstractQueryHit) {
			ITask task = null;
			if (object instanceof AbstractQueryHit) {
				task = ((AbstractQueryHit)object).getCorrespondingTask();
			} else if (object instanceof ITask) {
				task = (ITask)object;
			}
			if (task != null) {
				if (task instanceof AbstractRepositoryTask) {
					text += ((AbstractRepositoryTask)task).getTaskKey() + ": ";
				}
				
				text += task.getSummary();
				if (task.hasValidUrl()) {
					text += "\n" + task.getTaskUrl();
				} 
			} else {
				text += ((AbstractQueryHit)object).getSummary();
			}
		} else if (object instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery)object;
			text += query.getSummary();
			text += "\n" + query.getUrl();
		} else if (object instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) object;
			text = element.getSummary();
		}
		return text;
	}
}
