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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Ken Sueda
 */
public class RemoveFromCategoryAction extends Action {
	public static final String ID = "org.eclipse.mylar.tasklist.actions.remove";
	
	private final TaskListView view;

	public RemoveFromCategoryAction(TaskListView view) {
		this.view = view;
		setText("Remove From Category");
		setId(ID);
        setImageDescriptor(TaskListImages.REMOVE);
	}
	
	@Override
	public void run() {
		try {
			Object selectedObject = ((IStructuredSelection) this.view.getViewer().getSelection()).getFirstElement();		
			
//			if (selectedObject instanceof ITaskListElement &&
//				MylarTaskListPlugin.getDefault().getHandlerForElement((ITaskListElement)selectedObject) != null) {
//				
//				TreeItem item = this.view.getViewer().getTree().getSelection()[0];
//				ITaskListElement selectedElement = (ITaskListElement)selectedObject;
//				ITaskHandler handler = MylarTaskListPlugin.getDefault().getHandlerForElement(selectedElement);
//
//				if (item.getParentItem() != null) {
//					if (item.getParentItem().getData() instanceof IRepositoryQuery) {
//						MessageDialog.openInformation(Workbench.getInstance()
//								.getActiveWorkbenchWindow().getShell(), "Mylar Tasks",
//								"Tasks can not be deleted from a query.");
//						return;
//					}
//				
//					handler.itemRemoved(selectedElement, (ITaskCategory)item.getParentItem().getData());	
//				} else {
//					handler.itemRemoved(selectedElement, (ITaskCategory)item.getParentItem().getData());						
//				}
//			} else 
			if (selectedObject instanceof ITask && !((ITask)selectedObject).isLocal()) {
				ITask task = (ITask) selectedObject; 
				if (task.isActive()) {
					MessageDialog.openInformation(Workbench.getInstance()
							.getActiveWorkbenchWindow().getShell(), "Mylar Tasks",
							"Task must be deactivated in order to remove from category.");
					return;
				}
				
				TreeItem item = this.view.getViewer().getTree().getSelection()[0];
				if (item.getParentItem() != null && item.getParentItem().getData() instanceof TaskCategory) {
					TaskCategory category = (TaskCategory)item.getParentItem().getData();
					MylarTaskListPlugin.getTaskListManager().removeFromCategory(category, task);
				} else {
					MylarTaskListPlugin.getTaskListManager().removeFromRoot(task);
				}
				ITaskCategory cat = task.getCategory();
				if (cat != null) {
					String message = DeleteAction.genDeleteConfirmationMessage(task);		
					boolean deleteConfirmed = MessageDialog.openQuestion(
				            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
				            "Confirm delete", message);
					if (!deleteConfirmed) 
						return;
											
					MylarTaskListPlugin.getTaskListManager().deleteTask(task);
					MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier());//, task.getContextPath());
				}
				
				IWorkbenchPage page = MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
	
				// if we couldn't get the page, get out of here
				if (page != null) {
					try {
						this.view.closeTaskEditors((ITask) selectedObject, page);
					} catch (Exception e) {
						MylarStatusHandler.log(e, " remove failed");
					}
				}
			}
			this.view.getViewer().refresh();
		} catch (NullPointerException npe) {
			MylarStatusHandler.fail(npe, "Could not remove task from category, it may still be refreshing.", true);
		}
	}
}
