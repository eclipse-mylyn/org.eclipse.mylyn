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
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public class BugzillaHit implements ITaskListElement {

	private String description;
	private String priority;
	private int id;
	
	public BugzillaHit(String description, String priority, int id) {
		this.description = description;
		this.priority = priority;
		this.id = id;
	}
	public Image getTypeIcon() {
		return BugzillaImages.getImage(BugzillaImages.BUG);
	}

	public Image getStatusIcon() {
		return null;
	}

	public String getPriority() {
		return priority;
	}

	public String getDescription() {
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
		return "<UNKNOWN>";
	}
	public int getID() {
		
		return id;
	}

}
