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
		Object selectedObject = ((IStructuredSelection) ((TreeViewer) getViewer()).getSelection()).getFirstElement();
		Object currentTarget = getCurrentTarget();
		ITask taskToMove = null;
		if (selectedObject instanceof ITask) {
			taskToMove = (ITask)selectedObject;
		} else if (selectedObject instanceof IQueryHit) { 
			ITaskHandler handler = MylarTaskListPlugin.getDefault().getHandlerForElement((IQueryHit)selectedObject);
			if (handler != null) {
				taskToMove = handler.getCorrespondingTask((IQueryHit)selectedObject);
			}			
		} 
			
		if (taskToMove != null) {
			if (currentTarget instanceof TaskCategory) {
				MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory) currentTarget, taskToMove);
			} else if (currentTarget instanceof ITask) {
				ITask targetTask = (ITask)currentTarget;
				if (targetTask.getCategory() == null) {
					MylarTaskListPlugin.getTaskListManager().moveToRoot(taskToMove);
				} else {
					MylarTaskListPlugin.getTaskListManager().moveToCategory((TaskCategory)targetTask.getCategory(), taskToMove);
				}
			}
			return true;
		}
//		} else if (selectedObject instanceof ITaskListElement
//				&& MylarTaskListPlugin.getDefault().getHandlerForElement((ITaskListElement) selectedObject) != null) {
//
//			if (currentTargetObject instanceof TaskCategory) 
//			
//			// delegate the drop
//			MylarTaskListPlugin.getDefault().getHandlerForElement((ITaskListElement) selectedObject).dropItem(
//					(ITaskListElement) selectedObject, 
//					(TaskCategory) currentTargetObject);
//			// getViewer().setSelection(null);
//			return true;
//		}
		return false;
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
//				System.err.println(">>> " + getCurrentLocation());
				return true;
			} else {
				return false;
			}
		}

		return TextTransfer.getInstance().isSupportedType(transferType);
	}
}