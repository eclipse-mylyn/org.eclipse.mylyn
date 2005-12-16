/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * @author Mik Kersten
 */
class TaskListDropAdapter extends ViewerDropAdapter {
	
	public TaskListDropAdapter(Viewer viewer) {
		super(viewer);
		setFeedbackEnabled(true);
	}

	@Override
	public boolean performDrop(Object data) {
//		Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer()).getSelection()).getFirstElement();
		ISelection selection = ((TreeViewer)getViewer()).getSelection();
		Object currentTarget = getCurrentTarget();
		List<ITask> tasksToMove = new ArrayList<ITask>();
		for (Object selectedObject : ((IStructuredSelection) selection).toList()) {
			ITask toMove = null;
			if (selectedObject instanceof ITask) {
				toMove = (ITask)selectedObject;
			} else if (selectedObject instanceof IQueryHit) { 
				ITaskHandler handler = MylarTaskListPlugin.getDefault().getHandlerForElement((IQueryHit)selectedObject);
				if (handler != null) {
					toMove = handler.getCorrespondingTask((IQueryHit)selectedObject);
				}			
			} 
			if (toMove != null) {
				tasksToMove.add(toMove);
			}
		}
		
		for (ITask task : tasksToMove) {
			if (currentTarget instanceof TaskCategory) {
				MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory) currentTarget, task);
			} else if (currentTarget instanceof ITask) {
				ITask targetTask = (ITask)currentTarget;
				if (targetTask.getCategory() == null) {
					MylarTaskListPlugin.getTaskListManager().moveToRoot(task);
				} else {
					MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory)targetTask.getCategory(), task);
				}
			}
		}
		return true;
	}

	@Override
	public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
		Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer()).getSelection()).getFirstElement();
		if (selectedObject instanceof ITaskListElement && ((ITaskListElement) selectedObject).isDragAndDropEnabled()) {
			if (getCurrentTarget() instanceof TaskCategory) {
				return true;
			} else if (getCurrentTarget() instanceof ITaskListElement 
					&& (getCurrentLocation() == ViewerDropAdapter.LOCATION_AFTER
					   || getCurrentLocation() == ViewerDropAdapter.LOCATION_BEFORE)) {
				return true;
			} else {
				return false;
			}
		}

		return TextTransfer.getInstance().isSupportedType(transferType);
	}
}

//} else if (selectedObject instanceof ITaskListElement
//		&& MylarTaskListPlugin.getDefault().getHandlerForElement((ITaskListElement) selectedObject) != null) {
//
//	if (currentTargetObject instanceof TaskCategory) 
//	
//	// delegate the drop
//	MylarTaskListPlugin.getDefault().getHandlerForElement((ITaskListElement) selectedObject).dropItem(
//			(ITaskListElement) selectedObject, 
//			(TaskCategory) currentTargetObject);
//	// getViewer().setSelection(null);
//	return true;
//}