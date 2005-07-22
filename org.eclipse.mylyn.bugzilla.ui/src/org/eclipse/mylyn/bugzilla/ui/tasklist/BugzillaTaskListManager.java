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

package org.eclipse.mylar.bugzilla.ui.tasklist;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class BugzillaTaskListManager {

    private Map<String, BugzillaTask> bugzillaTaskRegistry = new HashMap<String, BugzillaTask>();
	
    // XXX we never delete anything from this registry
    
    public void addToBugzillaTaskRegistry(BugzillaTask task){
    	if(bugzillaTaskRegistry.get(task.getHandle()) == null){
    		bugzillaTaskRegistry.put(task.getHandle(), task);
    	}
    }
    
    public BugzillaTask getFromBugzillaTaskRegistry(String handle){
    	return bugzillaTaskRegistry.get(handle);
    }
    
    public Map<String, BugzillaTask> getBugzillaTaskRegistry(){
    	return bugzillaTaskRegistry;
    }
    
}
