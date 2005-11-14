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

package org.eclipse.mylar.java;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;
import org.eclipse.team.internal.core.subscribers.SubscriberChangeSetCollector;

/**
 * @author Mik Kersten
 */
public class TaskContextChangeSet extends ActiveChangeSet {

	private static final String LABEL_PREFIX = "Mylar Task";
	private List<IResource> resources;
	private ITask task;
	
	public TaskContextChangeSet(ITask task, SubscriberChangeSetCollector collector) {
		super(collector, LABEL_PREFIX);
		this.task = task;
		super.setTitle(LABEL_PREFIX + ": " + this.task.getDescription(true));
	}
	
	@Override
	public String getComment() {
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
	
	@Override
	public void remove(IResource resource) {
		super.remove(resource);
		resources.remove(resource);
	}

	@Override
	public void remove(IResource[] newResources) {
		super.remove(newResources);
		for (int i = 0; i < newResources.length; i++) resources.remove(newResources[i]);
	} 

	@Override
	public void add(IResource[] newResources) throws TeamException {
		super.add(newResources);
		for (int i = 0; i < newResources.length; i++) resources.add(newResources[i]);
	}

	@Override
	public IResource[] getResources() {
		if (MylarIdePlugin.getDefault() != null) {
			resources = MylarIdePlugin.getDefault().getInterestingResources();
		}
		return resources.toArray(new IResource[resources.size()]);
	}
	
    public boolean contains(IResource local) {
    	return resources.contains(local);
    }

}
