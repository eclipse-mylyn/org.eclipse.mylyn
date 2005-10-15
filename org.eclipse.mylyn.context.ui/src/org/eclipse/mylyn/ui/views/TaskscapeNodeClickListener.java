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
 * Created on Feb 17, 2005
  */
package org.eclipse.mylar.ui.views;

import org.eclipse.jface.viewers.*;
import org.eclipse.mylar.core.IMylarRelation;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarUiPlugin;



public class TaskscapeNodeClickListener implements IOpenListener {

    
    private final Viewer viewer;

    public TaskscapeNodeClickListener(Viewer viewer) {
        this.viewer = viewer;
    }

    public void open(OpenEvent event) {
        StructuredSelection selection = (StructuredSelection)viewer.getSelection();
        Object object = selection.getFirstElement();
        IMylarElement node = null;
        if(object instanceof IMylarElement){
            node = (IMylarElement)object ;
        } else if (!(object instanceof IMylarRelation)) {
        	IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
        	String handle = bridge.getHandleIdentifier(object);
        	node = MylarPlugin.getContextManager().getElement(handle);
        }
        if (node != null) MylarUiPlugin.getDefault().getUiBridge(node.getContentType()).open(node);
    }
}