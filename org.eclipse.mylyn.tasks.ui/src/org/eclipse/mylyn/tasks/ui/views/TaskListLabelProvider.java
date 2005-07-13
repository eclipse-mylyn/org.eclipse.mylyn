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
package org.eclipse.mylar.tasks.ui.views;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.tasks.AbstractCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.ITaskListElement;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskListLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {

	private Color backgroundColor = null;
	    
    public String getColumnText(Object obj, int columnIndex) {
    	if (obj instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) obj;
			switch (columnIndex) {
			case 0:
				return "";
			case 1:
				return "";
			case 2:
				if (element instanceof AbstractCategory) {
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
        	if (element instanceof AbstractCategory) {
        		return ((ITaskListElement)element).getIcon(); 
        	} else {
        		return ((ITaskListElement)element).getStatusIcon();
        	}        	
        } else if (columnIndex == 1) {
        	if (element instanceof AbstractCategory) {
        		return null;
        	}
        	return ((ITaskListElement)element).getIcon();
        } else {
        	return null;
        }
    }

    public Color getBackground(Object element) {
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
		  } else if (element instanceof AbstractCategory) {
			  return backgroundColor;
		  }
		  return null;
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
