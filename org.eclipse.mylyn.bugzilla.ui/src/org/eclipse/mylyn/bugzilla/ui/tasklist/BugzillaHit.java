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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.bugzilla.core.internal.HtmlStreamTokenizer;
import org.eclipse.mylar.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class BugzillaHit implements IQueryHit {

	private String description;
	private String priority;
	private int id;
	private BugzillaTask task;
	private String status;
	
	public BugzillaHit(String description, String priority, int id, BugzillaTask task, String status) {
		this.description = description;
		this.priority = priority;
		this.id = id;
		this.task = task;
		this.status = status;
	}
		
	public BugzillaTask getAssociatedTask(){
		return task;
	}
	
	public void setAssociatedTask(ITask task){
		if(task instanceof BugzillaTask)
			setAssociatedTask((BugzillaTask)task);
	}
	
	private void setAssociatedTask(BugzillaTask task){
		this.task = task;
	}
	
	public Image getIcon() {
		if(hasCorrespondingActivatableTask()){
			return task.getIcon();
		} else {
			return BugzillaImages.getImage(BugzillaImages.BUGZILLA_HIT);
		}
	}

	public Image getStatusIcon() {
		if (hasCorrespondingActivatableTask()) {
    		return task.getStatusIcon();
    	} else {
    		return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
    	}  
	}

	public String getPriority() {
		if(hasCorrespondingActivatableTask()){
			return task.getPriority();
		} else {
			return priority;
		}
	}

	public String getDescription(boolean label) {
		if(label){
			if(hasCorrespondingActivatableTask()){
				return task.getDescription(label);
			} else {
				return HtmlStreamTokenizer.unescape(description);
			}
		} else {
			if(hasCorrespondingActivatableTask()){
				return task.getDescription(label);
			} else {
				return description;
			}
		}
	}

	public String getHandle() {
		return getServerName()+"-"+getID();
	}

	public String getServerName() {
		// TODO need the right server name - get from the handle
		return "Bugzilla";
	}
	
	public int getID() {
		
		return id;
	}

	public String getIDString() {
		Integer bugId = new Integer(this.id);
		return bugId.toString();
	}

	public String getBugUrl() {
		return BugzillaRepository.getBugUrlWithoutLogin(id);
	}
	
	public boolean isDirectlyModifiable() {
		return false;
	}

	public ITask getOrCreateCorrespondingTask() {
		if(task == null){
			task = new BugzillaTask(this, true);
			BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry(task);
		} 
		return task;
	}
	
	public boolean hasCorrespondingActivatableTask() {
		return task != null;
	}

	public boolean isActivatable() {
		return true;
	}
	
	public boolean isDragAndDropEnabled() {
		return true;
	}

	public Color getForeground() {
        if ((task != null && task.isCompleted()) || isCompleted()){
        	return GRAY_VERY_LIGHT;
        } else {
        	return null;
        }
	}
	
	public Font getFont(){
    	if(hasCorrespondingActivatableTask()){
            return task.getFont();        
    	}
    	return null;
	}

	public boolean isCompleted() {
		if (status.startsWith("RESO") || status.startsWith("CLO") || status.startsWith("VERI")) {
			return true;
		}
		return false;
	}

	public String getToolTipText() {
		if(hasCorrespondingActivatableTask()) {
			return task.getToolTipText();
		} else {
			return getDescription(true);
		}
	}

	public void setDescription(String description) {
		// can't set the description to anything
	}

	public String getStringForSortingDescription() {
		return getID()+"";
	}

	public void setHandle(String id) {
		// can't change the handle
	}
}
