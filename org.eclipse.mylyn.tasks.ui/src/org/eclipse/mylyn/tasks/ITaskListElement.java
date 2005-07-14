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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ken Sueda
 */
public interface ITaskListElement {
	
    public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
    public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

    public Color GRAY_VERY_LIGHT  = new Color(Display.getDefault(), 200, 200, 200); // TODO: use theme?
    	
	public abstract Image getIcon();
	
	public abstract Image getStatusIcon();
    
    public abstract String getPriority();
    
    public abstract String getDescription(boolean label);
    
    public abstract String getHandle();
    
    /**
     * Used for wrapping bugzilla hits
     */
    public ITask getCorrespondingActivatableTask();

	public abstract boolean hasCorrespondingActivatableTask();
	
    public abstract boolean isDirectlyModifiable();
    
    public abstract boolean isActivatable();

	public abstract boolean isDragAndDropEnabled();

	public abstract Color getForeground();

	public abstract Font getFont();
}
