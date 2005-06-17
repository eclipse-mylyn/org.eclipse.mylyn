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
 * Created on Jan 13, 2005
  */
package org.eclipse.mylar.tasks.bugzilla;

import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.tasks.BugzillaTask;


/**
 * @author Mik Kersten
 * @author Eric Booth
 */
public class BugzillaContentProvider {
	
    public BugzillaContentProvider() {
    	// don't have any initialization to do
    }
    
    /**
     * @return  String containing bug priority and label, e.g. "[12345] P2: fix failing test"
     */
    public String getBugzillaDescription(BugzillaTask bugTask) {
		if (bugTask == null) return "<no info>";
		
		String prefix = //((bugTask.isDirty()) ? ">" : "") +
					    "<" + BugzillaTask.getBugId(bugTask.getHandle()) + ">: ";
    	
		if (bugTask.getState() == BugzillaTask.BugTaskState.DOWNLOADING) {
			return prefix + ": <Downloading bug report from server...>";
		} else if (bugTask.getState() == BugzillaTask.BugTaskState.OPENING) {
			return prefix + ": <Opening bug report in editor...>";
		} else if (bugTask.getState() == BugzillaTask.BugTaskState.COMPARING) {
			return prefix + ": <Comparing bug report with server...>";
		} else if (bugTask.getState() == BugzillaTask.BugTaskState.WAITING) {
			return prefix + ": <Waiting to check server...>";
		}
		
    	// generate the label
		if (bugTask.isBugDownloaded()) {
			BugReport report = bugTask.getBugReport();	
			return prefix + report.getSummary();
		}
		else {
			return prefix + ": <could not find bug>";
		}
    }

}
