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
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.ui.MylarUiPlugin;



public class TaskscapeNodeClickListener implements IOpenListener {

    
    private final Viewer viewer;

    public TaskscapeNodeClickListener(Viewer viewer) {
        this.viewer = viewer;
    }

    public void open(OpenEvent event) {
        StructuredSelection selection = (StructuredSelection)viewer.getSelection();
        Object obj = selection.getFirstElement();
        if(obj instanceof IMylarContextNode){
            IMylarContextNode node = (IMylarContextNode)obj ;

            MylarUiPlugin.getDefault().getUiBridge(node.getStructureKind()).open(node);
        } 
    }
}