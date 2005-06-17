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
import org.eclipse.ui.*;

/**
 * @author Mik Kersten
 */
public class InterestDecrementAction implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

    public static final String COMMAND_ID = "org.eclipse.mylar.ui.interest.decrement";
        
    public void run(IAction action) { 
    	// Does nothing, since this is handled by command monitor
    }  
    
    public void dispose() { 
    	// don't care when we are disposed
    }
    
    public void init(IViewPart view) {
    	// don't need to do anything here
    }
    public void selectionChanged(IAction action, ISelection selection) { 
    	// don't care about selectio changes
    }

    public void init(IWorkbenchWindow window) { 
    	// don't have anything to initialize
    }
}
