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

package org.eclipse.mylar.java.internal;

import java.util.Arrays;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;

/**
 * @author Mik Kersten
 */
public class MylarDynamicChangeSet extends ActiveChangeSet {

	public MylarDynamicChangeSet() {
		super(null, "Mylar Dynamic Change Set");
	}
	
	@Override
	public String getComment() {
		ITask task = TaskListView.getDefault().getSelectedTask();
		if (task != null) {
			return generateComment(task);
		} else {
			return "";
		}
	}
	
	@Override
	public IResource[] getResources() {
		return MylarIdePlugin.getDefault().getInterestingResources();
	}
	
    public boolean contains(IResource local) {
//    	List<IResource> resources = new ArrayList<IResource>();
        return Arrays.asList((getResources())).contains(local);
    }

	protected String generateComment(ITask task) {
		String prefix = "";
		if (task.isCompleted()) {
			prefix = "Completed "; 
		} else {
			prefix = "Progress on ";
		}
		if (task.isDirectlyModifiable()) {
			return prefix + task.getDescription(false);
		} else { // bug report
			return prefix + "Bug " + task.getDescription(false);
		}
	}

}
