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

package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITaskListCategory;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
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
//        setImageDescriptor(TasklistImages.REMOVE);
	}
	
	@Override
	public void run() {
		try {
			Object selectedObject = ((IStructuredSelection) this.view.getViewer().getSelection()).getFirstElement();		
			
			if (selectedObject instanceof ITaskListElement &&
				MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITaskListElement)selectedObject) != null) {
				
				TreeItem item = this.view.getViewer().getTree().getSelection()[0];
				ITaskListElement selectedElement = (ITaskListElement)selectedObject;
				ITaskHandler handler = MylarTasklistPlugin.getDefault().getTaskHandlerForElement(selectedElement);
				if (item.getParentItem() != null) {
					handler.itemRemoved(selectedElement, (ITaskListCategory)item.getParentItem().getData());	
				} 
			} else if (selectedObject instanceof ITask) {
				ITask task = (ITask) selectedObject;
				if (task.isActive()) {
					MessageDialog.openError(Workbench.getInstance()
							.getActiveWorkbenchWindow().getShell(), "Remove failed",
							"Task must be deactivated in order to remove from category.");
					return;
				}
				ITaskListCategory cat = task.getCategory();
				if (cat != null) {
//					cat.removeTask(task);				
//				} else {
					String message = task.getDeleteConfirmationMessage();			
					boolean deleteConfirmed = MessageDialog.openQuestion(
				            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
				            "Confirm delete", message);
					if (!deleteConfirmed) 
						return;
											
					MylarTasklistPlugin.getTaskListManager().deleteTask(task);
					MylarPlugin.getContextManager().contextDeleted(task.getHandleIdentifier(), task.getPath());
				}
				
				IWorkbenchPage page = MylarTasklistPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
	
				// if we couldn't get the page, get out of here
				if (page != null) {
					try {
						this.view.closeTaskEditors((ITask) selectedObject, page);
					} catch (Exception e) {
						MylarPlugin.log(e, " remove failed");
					}
				}
			}
			this.view.getViewer().refresh();
		} catch (NullPointerException npe) {
			MylarPlugin.fail(npe, "Could not remove task from category, it may still be refreshing.", true);
		}
	}
}
