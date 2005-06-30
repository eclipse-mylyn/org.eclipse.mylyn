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

package org.eclipse.mylar.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.BugzillaQueryCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class DeleteAction extends Action {
	
	public static final String ID = "org.eclipse.mylar.tasks.actions.delete";
		
	private final TaskListView view;

	public DeleteAction(TaskListView view) {
		this.view = view;
		setText("Delete");
		setId(ID);
        setImageDescriptor(MylarImages.REMOVE);
	}
	
	@Override
	public void run() {     
        MylarPlugin.getDefault().actionObserved(this);
	    boolean deleteConfirmed = MessageDialog.openQuestion(
	            Workbench.getInstance().getActiveWorkbenchWindow().getShell(),
	            "Confirm delete", 
	            "Delete selected item?");
	    if (!deleteConfirmed) { 
	        return;
	    } else {
	        Object selectedObject = ((IStructuredSelection)this.view.getViewer().getSelection()).getFirstElement();
	        if (selectedObject instanceof Task) {
				MylarTasksPlugin.getTaskListManager().deleteTask((Task)selectedObject);
				MylarPlugin.getTaskscapeManager().taskDeleted(((Task)selectedObject).getHandle(), ((Task)selectedObject).getPath());
				IWorkbenchPage page = MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				
				// if we couldn't get the page, get out of here
				if (page == null)
					return;
				try{
	                this.view.closeTaskEditors((ITask)selectedObject, page);
	            }catch(Exception e){
	            	MylarPlugin.log(e, " deletion failed");
	            }
	        } else if (selectedObject instanceof TaskCategory) {
	        	TaskCategory cat = (TaskCategory) selectedObject;
	        	for (ITask task : cat.getChildren()) {
	        		MylarPlugin.getTaskscapeManager().taskDeleted(task.getHandle(), task.getPath());
	        		IWorkbenchPage page = MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();    					
					if (page == null)
						return;
					try{
	                    this.view.closeTaskEditors(task, page);
	                }catch(Exception e){
	                	MylarPlugin.log(e, " deletion failed");
	                }
	        	}
	        	MylarTasksPlugin.getTaskListManager().deleteCategory((TaskCategory)selectedObject);
	        }  else if (selectedObject instanceof BugzillaQueryCategory) {
	        	BugzillaQueryCategory cat = (BugzillaQueryCategory) selectedObject;
	        	MylarTasksPlugin.getTaskListManager().deleteCategory(cat);
	        }
	    }
	    this.view.getViewer().refresh();
	}
}