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
 * Created on Apr 18, 2005
  */
package org.eclipse.mylar.tasks.bugzilla.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.ui.MylarImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class BugzillaNodeLabelProvider implements ILabelProvider {

    public Image getImage(Object element) {
        return MylarImages.getImage(MylarImages.BUG); 
    }

    /**
     * TODO: slow?
     */
    public String getText(Object element) {
        ITaskscapeNode node = (ITaskscapeNode)element;
        String name = MylarTasksPlugin.getStructureBridge().getName(
                MylarTasksPlugin.getStructureBridge().getObjectForHandle(node.getElementHandle())
        );
        return name;
    }

    public void addListener(ILabelProviderListener listener) {
    	// don't need to worry about listeners
    }

    public void dispose() {
    	// don't care about dispose
    }

    public boolean isLabelProperty(Object element, String property) {
        return false; 
    }

    public void removeListener(ILabelProviderListener listener) {
    	// don't need to worry about listeners 
    }
} 
