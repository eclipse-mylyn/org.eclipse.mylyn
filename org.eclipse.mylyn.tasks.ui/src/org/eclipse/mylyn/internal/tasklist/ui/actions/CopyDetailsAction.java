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
package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

/**
 * @author Mik Kersten
 */
public class CopyDetailsAction extends Action {

	private static final String LABEL = "Copy Details";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.copy";

	private TaskListView view;

	public CopyDetailsAction(TaskListView view) {
		this.view = view;
		setText(LABEL);
		setToolTipText(LABEL);
		setId(ID);
		setImageDescriptor(TaskListImages.COPY);
		setAccelerator(SWT.MOD1 + 'c');
	}

	@Override
	public void run() {
		ISelection selection = this.view.getViewer().getSelection();
		Object object = ((IStructuredSelection) selection).getFirstElement();
		String text = "";
		if (object instanceof ITask || object instanceof AbstractQueryHit) {
			ITask task = null;
			if (object instanceof AbstractQueryHit) {
				task = ((AbstractQueryHit)object).getCorrespondingTask();
			} else if (object instanceof ITask) {
				task = (ITask)object;
			}
			if (task != null) {
				text = task.getDescription();
				if (task.hasValidUrl()) {
					text += "\n" + task.getUrl();
				} 
			} else {
				text += ((AbstractQueryHit)object).getDescription();
			}
		} else if (object instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery)object;
			text += query.getDescription();
			text += "\n" + query.getQueryUrl();
		} else if (object instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) object;
			text = element.getDescription();
		} 

		// HACK: this should be done using proper copying
		StyledText styledText = new StyledText(view.getDummyComposite(), SWT.NULL);
		styledText.setText(text);
		styledText.selectAll();
		styledText.copy();
		styledText.dispose();
	}
}
