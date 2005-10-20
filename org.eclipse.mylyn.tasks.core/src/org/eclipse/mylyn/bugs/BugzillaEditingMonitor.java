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
 * Created on Apr 27, 2005
  */
package org.eclipse.mylar.bugs;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylar.bugzilla.ui.outline.BugzillaReportSelection;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTaskEditor;
import org.eclipse.mylar.core.AbstractInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author Mik Kersten
 */
public class BugzillaEditingMonitor extends AbstractInteractionMonitor {

    public BugzillaEditingMonitor() {
        super();
    }

    @Override
    protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
        if(!(part instanceof AbstractBugEditor) && !(part instanceof BugzillaTaskEditor))
            return;
        
        if(selection instanceof StructuredSelection){
            StructuredSelection ss = (StructuredSelection)selection;
            Object object = ss.getFirstElement();
            if(object instanceof BugzillaReportSelection) super.handleElementSelection(part, object);
        }
    }

}
