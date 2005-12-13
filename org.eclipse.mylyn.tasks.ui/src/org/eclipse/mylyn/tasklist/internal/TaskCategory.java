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
/*
 * Created on Dec 26, 2004
 */
package org.eclipse.mylar.tasklist.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.MylarTaskListPrefConstants;
import org.eclipse.mylar.tasklist.ui.CategoryEditorInput;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class TaskCategory implements ITaskCategory, Serializable {

	private static final long serialVersionUID = 3834024740813027380L;

	private List<ITask> tasks = new ArrayList<ITask>();

	protected String description = "";

	private String handle = "";

	private boolean isArchive = false;

	public TaskCategory(String description) {
		this.description = description;
	}

	public String getDescription(boolean label) {
		return description;
	}

	public String getHandleIdentifier() {
		return handle;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public Image getStatusIcon() {
		return null;
	}

	public Image getIcon() {
		if (isArchive()) {
			return TaskListImages.getImage(TaskListImages.CATEGORY_ARCHIVE);
		} else {
			return TaskListImages.getImage(TaskListImages.CATEGORY);
		}
	}

	public String getPriority() {
		String highestPriority = MylarTaskListPlugin.PriorityLevel.P5.toString();
		if (tasks.isEmpty()) {
			return MylarTaskListPlugin.PriorityLevel.P1.toString();
		}
		for (ITask task : tasks) {
			if (highestPriority.compareTo(task.getPriority()) > 0) {
				highestPriority = task.getPriority();
			}
		}
		return highestPriority;
	}

	void addTask(ITask task) {
		tasks.add(task);
	}

	/**
	 * HACK: public so it can be used by other externalizers
	 */
	public void internalAddTask(ITask task) {
		tasks.add(task);
	}

	public void removeTask(ITask task) {
		tasks.remove(task);
	}

	public List<ITask> getChildren() {
		return tasks;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object instanceof TaskCategory) {
			TaskCategory compare = (TaskCategory) object;
			return this.getDescription(false).equals(compare.getDescription(false));
		} else {
			return false;
		}
	}

	public ITask getOrCreateCorrespondingTask() {
		return null;
	}

	public boolean hasCorrespondingActivatableTask() {
		return false;
	}

	public boolean isLocal() {
		return true;
	}

	public boolean isActivatable() {
		return false;
	}

	public boolean isDragAndDropEnabled() {
		return false;
	}

	public Color getForeground() {
		for (ITask child : getChildren()) {
			if (child.isActive())
				return MylarTaskListPrefConstants.ACTIVE_TASK;
		}
		return null;
	}

	public Font getFont() {
		for (ITask child : getChildren()) {
			if (child.isActive())
				return TaskListImages.BOLD;
		}
		return null;
	}

	public boolean isCompleted() {
		return false;
	}

	public String getToolTipText() {
		if (tasks.size() == 1) {
			return "1 task";
		} else {
			return tasks.size() + " tasks";
		}
	}

	public boolean isArchive() {
		return isArchive;
	}

	public void setIsArchive(boolean isArchive) {
		this.isArchive = isArchive;
		;
	}

	public String getStringForSortingDescription() {
		return getDescription(true);
	}

	public void openCategoryInEditor(boolean offline) {
		Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
			public void run() {
				openCategory();
			}
		});
	}

	public void openCategory() {
		IWorkbenchPage page = MylarTaskListPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null) {
			return;
		}

		IEditorInput input = new CategoryEditorInput(this);
		try {
			page.openEditor(input, MylarTaskListPrefConstants.CATEGORY_EDITOR_ID);
		} catch (PartInitException ex) {
			ErrorLogger.log(ex, "open failed");
		}
	}

	public String toString() {
		return getDescription(false);
	}
}
