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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.mylar.ui.MylarUiPlugin;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class CreateTaskAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasks.actions.create.task";
		
	private final TaskListView view;

	public CreateTaskAction(TaskListView view) {
		this.view = view;
		setText("Add Task");
        setToolTipText("Add Task");
        setId(ID);
        setImageDescriptor(MylarImages.TASK_NEW);
	}
	
    @Override
    public void run() {
        MylarPlugin.getDefault().actionObserved(this);
        String label = this.view.getLabelNameFromUser("task");
        if(label == null) return;
        Task newTask = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), label);
        
        Object selectedObject = ((IStructuredSelection)this.view.getViewer().getSelection()).getFirstElement();
        if (selectedObject instanceof TaskCategory){
        	newTask.setCategory((TaskCategory)selectedObject);
            ((TaskCategory)selectedObject).addTask(newTask);
        } 
//            else if (selectedObject instanceof Task) {
//            	ITask t = (ITask) selectedObject;
//            	newTask.setParent(t);
//            	t.addSubTask(newTask);
//            }
        else {            	
            MylarTasksPlugin.getTaskListManager().getTaskList().addRootTask(newTask);                
        }  
        MylarUiPlugin.getDefault().setHighlighterMapping(
                newTask.getHandle(), 
                MylarUiPlugin.getDefault().getDefaultHighlighter().getName());
        this.view.getViewer().refresh();
    }
}