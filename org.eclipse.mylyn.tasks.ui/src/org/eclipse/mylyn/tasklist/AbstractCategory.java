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

package org.eclipse.mylar.tasklist;

import java.util.List;

import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public abstract class AbstractCategory implements ITaskListElement {

	private String priority = "";
	protected String description = "";
	private String handle = "";
		
	public AbstractCategory(String description) {
		this.description = description;
	}
	
	public Image getIcon() {
		return null;
	}

	public String getPriority() {
		return priority;
	}

	public String getDescription(boolean label) {
		return description;
	}

	public String getHandle() {
		return handle;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Image getStatusIcon() {
		return null;
	}
	
	public abstract List<? extends ITaskListElement> getChildren();
}
