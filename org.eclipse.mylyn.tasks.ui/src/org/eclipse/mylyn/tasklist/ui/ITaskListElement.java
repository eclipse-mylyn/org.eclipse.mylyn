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
package org.eclipse.mylar.tasklist.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken Sueda
 */
public interface ITaskListElement {
	
	public abstract Image getIcon();

    public abstract Image getStatusIcon();
 
	public abstract boolean isDragAndDropEnabled();
	
    public abstract String getPriority();
    
    public abstract String getDescription(boolean truncate);
    
    public abstract void setDescription(String description);
    
    public abstract String getHandleIdentifier();
    
    public abstract void setHandle(String id);
	
    public abstract boolean isLocal();
    
    public abstract boolean isActivatable();

	public abstract Color getForeground();

	public abstract Font getFont();
	
	public abstract String getToolTipText();
	
	public abstract boolean isCompleted();

	public abstract String getStringForSortingDescription();
}
