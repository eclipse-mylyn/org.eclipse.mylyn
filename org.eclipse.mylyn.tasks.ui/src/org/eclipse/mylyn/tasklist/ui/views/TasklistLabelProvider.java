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
 * Created on Feb 18, 2005
  */
package org.eclipse.mylar.tasklist.ui.views;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskCategory;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.ui.ITaskHighlighter;
import org.eclipse.mylar.tasklist.ui.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TasklistLabelProvider extends LabelProvider implements IColorProvider, ITableLabelProvider, ITableColorProvider, ITableFontProvider {

	private Color backgroundColor = null;
	
	@Override
	public String getText(Object obj){
		return getColumnText(obj, 3);
	}
	
    public String getColumnText(Object obj, int columnIndex) {
    	if (obj instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) obj;
			switch (columnIndex) {
			case 0:
				return null;
			case 1:
				return null;
			case 2:
				if (element instanceof ITaskCategory || element instanceof IQuery) {
					return null;
				}
				return element.getPriority();
			case 3:
				return element.getDescription(false);
			}
		}
    	return null;
    }
   
    public Image getIcon(ITask task) {
    	String issueReportUrl = task.getIssueReportURL();
		if (issueReportUrl != null && !issueReportUrl.trim().equals("") && !issueReportUrl.equals("http://")) {
			return TaskListImages.getImage(TaskListImages.TASK_WEB);
		} else {
			return TaskListImages.getImage(TaskListImages.TASK);
		}
    }
    
    public Image getColumnImage(Object element, int columnIndex) {        
        if (! (element instanceof ITaskListElement)) { 
        	return null;
        }
        if (columnIndex == 0) {
        	if (element instanceof ITaskCategory || element instanceof IQuery) {
        	
        		return ((ITaskListElement)element).getIcon(); 
        	} else {
//        		return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
        		return ((ITaskListElement)element).getStatusIcon();
        	}        	
        } else if (columnIndex == 1) {
        	if (element instanceof ITaskCategory || element instanceof IQuery) {
        		return null;
        	}
        	return ((ITaskListElement)element).getIcon();
        } else {
        	return null;
        }
    }

    public void setBackgroundColor(Color c) {
    	this.backgroundColor = c;
    }

	public Color getForeground(Object object, int columnIndex) {
    	if (object instanceof ITaskCategory) {
    		for (ITask child : ((ITaskCategory)object).getChildren()) {
    			if (child.isActive())
    				return TaskListImages.COLOR_TASK_ACTIVE;
    		}
    	} else if (object instanceof IQuery) {
    		for (ITaskListElement child : ((IQuery)object).getHits()) {
    			if (child instanceof IQueryHit) {
    				ITask task = ((IQueryHit)child).getCorrespondingTask();
    				if (task != null && task.isActive()) {
    					return TaskListImages.COLOR_TASK_ACTIVE;
    				}
    			}
    		}
    	} else if (object instanceof IQueryHit && ((IQueryHit)object).getCorrespondingTask() == null) {
        	IQueryHit hit = (IQueryHit)object;
        	if (hit.isCompleted()) {
        		return TaskListImages.COLOR_TASK_COMPLETED;
        	}
        } else if (object instanceof ITaskListElement) {
        	ITask task = getAssociatedTask((ITaskListElement)object);
        	if (task != null) {
        		if (task.isCompleted()) {
        			return TaskListImages.COLOR_TASK_COMPLETED;
        		} else if (task.isActive()) {
        			return TaskListImages.COLOR_TASK_ACTIVE;
        		} else if (task.isPastReminder()) {
        			return TaskListImages.COLOR_TASK_OVERDUE;
        		}
        	}
        } 
        return null;
	}

	private ITask getAssociatedTask(ITaskListElement element) {
		if (element instanceof ITask) {
    		return (ITask)element;
    	} else if (element instanceof IQueryHit) {
    		return ((IQueryHit)element).getCorrespondingTask();
    	} else {
    		return null;
    	}
	}

	public Color getBackground(Object element, int columnIndex) {
    	try {
			  if (element instanceof ITask) {
			      ITask task = (ITask)element;
			      ITaskHighlighter highlighter = MylarTaskListPlugin.getDefault().getHighlighter();
			      if (highlighter != null) {
			    	  return highlighter.getHighlightColor(task); 
			      }
			  } else if (element instanceof ITaskCategory) {
				  ITaskCategory category = (ITaskCategory)element;
				  if (category.isArchive()) {
					  return TaskListImages.BACKGROUND_ARCHIVE;
				  } else {
					  return backgroundColor;
				  }
			  } else if (element instanceof IQuery) {
				  return backgroundColor;
			  } 
		} catch (Exception e) {
			ErrorLogger.fail(e, "Could not get background color", false);
		}
		return TaskListImages.BACKGROUND_WHITE;
  }

	public Font getFont(Object element, int columnIndex) {
        if (element instanceof ITaskListElement) {
        	ITaskListElement task = (ITaskListElement)element;
        	return task.getFont();
        }
        return null;
	}

	public Color getForeground(Object element) {
		return getForeground(element, 0);
	}

	public Color getBackground(Object element) {
		return getBackground(element, 0);
	}
}
