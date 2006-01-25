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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.mylar.internal.tasklist.ui.TaskListUiUtil;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.IRepositoryQuery;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskCategory;

/**
 * @author Mik Kersten
 */
public class OpenTaskListElementAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.open";

	private final StructuredViewer viewer;

	/**
	 * @param view
	 */
	public OpenTaskListElementAction(StructuredViewer view) {
		this.viewer = view;
		setText("Open");
		setToolTipText("Open Task List Element");
		setId(ID);
	}

	@Override
	public void run() {
		ISelection selection = viewer.getSelection();
		Object element = ((IStructuredSelection) selection).getFirstElement();
		if (element instanceof ITask) {
			TaskListUiUtil.openEditor((ITask)element);
		} else if (element instanceof IQueryHit) {
			TaskListUiUtil.openEditor((IQueryHit)element);
		} else if (element instanceof ITaskCategory) {
			TaskListUiUtil.openEditor((ITaskCategory)element);
		} else if (element instanceof IRepositoryQuery) {
			
		}
		
//		if (obj instanceof ITaskListElement) {
//			ITaskListElement element = (ITaskListElement) obj;
//			ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getHandlerForElement(element);
//			if (taskHandler != null) {
//				taskHandler.itemOpened(element);
//			} else {
//				if (element instanceof ITask) {
//					((Task) element).openTaskInEditor(false);
//				} else if (element instanceof ITaskCategory) {
//					((TaskCategory) element).openCategoryInEditor(false);
//				}
//			}
//		}
//		this.view.getViewer().refresh(obj);
	}
}
