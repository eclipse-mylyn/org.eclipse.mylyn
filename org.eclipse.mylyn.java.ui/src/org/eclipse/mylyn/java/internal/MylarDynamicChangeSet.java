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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
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
		List<IResource> interestingResources = new ArrayList<IResource>();
		Set<IMylarElement> resourceElements = MylarPlugin.getContextManager().getInterestingResources(
				MylarPlugin.getContextManager().getActiveContext());

		for (IMylarElement element : resourceElements) {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element.getContentType());
			Object object = bridge.getObjectForHandle(element.getHandleIdentifier());
			if (object instanceof IResource) {
				interestingResources.add((IResource)object);
			} else if (object instanceof IAdaptable) {
				Object adapted = ((IAdaptable)object).getAdapter(IResource.class);
				if (adapted instanceof IResource) {
					interestingResources.add((IResource)adapted);
				}
			}
		}
		
		return interestingResources.toArray(new IResource[interestingResources.size()]);
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
