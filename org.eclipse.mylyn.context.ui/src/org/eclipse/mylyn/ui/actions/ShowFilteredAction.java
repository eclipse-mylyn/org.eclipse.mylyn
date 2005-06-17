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
 * Created on Jul 27, 2004
  */
package org.eclipse.mylar.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.*;


/**
 * @author Mik Kersten
 */
public class ShowFilteredAction implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

    public void init(IViewPart view) {
    	// don't have anything to initialize
    }

    public void run(IAction action) {
        MylarPlugin.getTaskscapeManager().tempRaiseChildrenForSelected();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    	// don't care about selection changes
    }

    public void dispose() {
    	// don't care when we are disposed
    }

    public void init(IWorkbenchWindow window) {
    	// don't have anything to initialize
    }

}
