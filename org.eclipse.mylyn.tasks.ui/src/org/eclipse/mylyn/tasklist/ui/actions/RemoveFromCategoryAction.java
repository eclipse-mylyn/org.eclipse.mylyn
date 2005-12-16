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
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskHandler;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
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
//        setImageDescriptor(TaskListImages.REMOVE);
	}
	
	@Override
	public void run() {
		try {
			Object selectedObject = ((IStructuredSelection) this.view.getViewer().getSelection()).getFirstElement();		
			
			if (selectedObject instanceof ITaskListElement &&
				MylarTaskListPlugin.getDefault().getHandlerForElement((ITaskListElement)selectedObject) != null) {
				
				TreeItem item = this.view.getViewer().getTree().getSelection()[0];
				ITaskListElement selectedElement = (ITaskListElement)selectedObject;
				ITaskHandler handler = MylarTaskListPlugin.getDefault().getHandlerForElement(selectedElement);

				if (item.getParentItem().getData() instanceof IQuery) {
					MessageDialog.openInformation(Workbench.getInstance()
							.getActiveWorkbenchWindow().getShell(), "Mylar Tasks",
							"Tasks can not be deleted from a query.");
					return;
				}
				
				if (item.getParentItem() != null) {
					handler.itemRemoved(selectedElement, (ITaskCategory)item.getParentItem().getData());	
				} 
			} else if (selectedObject instanceof ITask) {
				ITask task = (ITask) selectedObject;
				if (task.isActive()) {
					MessageDialog.openInformation(Workbench.getInstance()
							.getActiveWorkbenchWindow().getShell(), "Mylar Tasks",
							"Task must be deactivated in order to remove from category.");
					return;
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
						ErrorLogger.log(e, " remove failed");
					}
				}
			}
			this.view.getViewer().refresh();
		} catch (NullPointerException npe) {
			ErrorLogger.fail(npe, "Could not remove task from category, it may still be refreshing.", true);
		}
	}
}
