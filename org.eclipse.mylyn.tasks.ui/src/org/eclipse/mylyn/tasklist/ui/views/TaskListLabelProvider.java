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
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ICategory;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskListLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {

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
				return "";
			case 1:
				return "";
			case 2:
				if (element instanceof ICategory || element instanceof IQuery) {
					return "";
				}
				return element.getPriority();
			case 3:
				return element.getDescription(true);
			}
		}
    	return "";
    }

    public Font getFont(Object element) {
        if (element instanceof ITaskListElement) {
        	ITaskListElement task = (ITaskListElement)element;
        	return task.getFont();
        }
        return null;
    }
   
    public Image getColumnImage(Object element, int columnIndex) {        
        if (! (element instanceof ITaskListElement)) { 
        	return null;
        }
        if (columnIndex == 0) {
        	if (element instanceof ICategory || element instanceof IQuery) {
        		return ((ITaskListElement)element).getIcon(); 
        	} else {
        		return ((ITaskListElement)element).getStatusIcon();
        	}        	
        } else if (columnIndex == 1) {
        	if (element instanceof ICategory || element instanceof IQuery) {
        		return null;
        	}
        	return ((ITaskListElement)element).getIcon();
        } else {
        	return null;
        }
    }

    public Color getBackground(Object element) {
    	try {
    	// XXX refactored highlighters
			  if (element instanceof ITask) {
	//		      ITask task = (ITask)element;
	//		      Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForTaskId("" + task.getHandle());
	//		      if (highlighter != null) return highlighter.getHighlightColor();
	//		  } 
	//		  else if (element instanceof BugzillaHit) {
	//			  BugzillaHit hit = (BugzillaHit)element;
	//			  BugzillaTask task = hit.getAssociatedTask();
	//			  if(task != null){
	//		          Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForTaskId("" + task.getHandle());
	//		          if (highlighter != null) return highlighter.getHighlightColor();
	//			  }
			  } else if (element instanceof ICategory) {
				  ICategory category = (ICategory)element;
				  if (category.isArchive()) {
					  return TaskListImages.BACKGROUND_ARCHIVE;
//					  return new Color(Display.getCurrent(), 
//							  Math.max(0, backgroundColor.getRed()-30),
//							  Math.max(0, backgroundColor.getGreen()-30),
//							  200);
				  } else {
					  return backgroundColor;
				  }
			  } else if (element instanceof IQuery) {
				  return backgroundColor;
			  }
		} catch (Exception e) {
			MylarPlugin.log("Could not get background color", this);
		}
		return TaskListImages.BACKGROUND_WHITE;
    }
    
    public Color getForeground(Object element) {
        if (element instanceof ITaskListElement) {
        	ITaskListElement task = (ITaskListElement)element;
            return task.getForeground();
        } 
        return null;
    }
    
    public void setBackgroundColor(Color c) {
    	this.backgroundColor = c;
    }
}
