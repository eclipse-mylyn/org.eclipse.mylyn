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

package org.eclipse.mylar.tasks;

import org.eclipse.mylar.bugzilla.BugzillaImages;
import org.eclipse.mylar.bugzilla.core.BugzillaRepository;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class BugzillaHit implements ITaskListElement {

	private String description;
	private String priority;
	private int id;
	private BugzillaTask task;
	
	public BugzillaHit(String description, String priority, int id, BugzillaTask task) {
		this.description = description;
		this.priority = priority;
		this.id = id;
		this.task = task;
	}
	
	public boolean isTask(){
		return task != null;
	}
	
	public BugzillaTask getAssociatedTask(){
		return task;
	}
	
	public void setAssociatedTask(BugzillaTask task){
		this.task = task;
	}
	
	public Image getIcon() {
		if(isTask()){
			return task.getIcon();
		} else {
			return BugzillaImages.getImage(BugzillaImages.BUG);
		}
	}

	public Image getStatusIcon() {
		if (isTask()) {
    		return task.getStatusIcon();
    	} else {
    		return MylarImages.getImage(MylarImages.TASK_INACTIVE);
    	}  
	}

	public String getPriority() {
		return priority;
	}

	public String getDescription(boolean label) {
		return description;
	}

	public String getHandle() {
		return getServerName()+"-"+getID();
	}
	public void setDescription(String description) {
		this.description = description;
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
		return BugzillaRepository.getBugUrl(id);
	}
}
