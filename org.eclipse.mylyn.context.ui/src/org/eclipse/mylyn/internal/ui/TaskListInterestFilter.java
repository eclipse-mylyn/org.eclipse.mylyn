/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.ui;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.ui.InterestFilter;

/**
 * Goal is to have this reuse as much of the super as possible.
 * 
 * @author Mik Kersten
 */
public class TaskListInterestFilter extends InterestFilter {

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		try {
			if (!(viewer instanceof StructuredViewer))
				return true;
			if (!containsMylarInterestFilter((StructuredViewer) viewer))
				return true;

			IMylarElement node = null;
			if (element instanceof IMylarElement) {
				node = (IMylarElement) element;
			} else {
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element);
				if (!bridge.canFilter(element)) {
					return true;
				}
				if (isImplicitlyInteresting(element, bridge))
					return true;
//				if (isImplicitlyUninteresting(element, bridge))
//					return true;
				
				String handle = bridge.getHandleIdentifier(element);
				node = MylarPlugin.getContextManager().getActivityHistoryMetaContext().get(handle);
			}
			if (node != null) {
				if (node.getInterest().isPredicted()) {
					return false;
				} else {
					return node.getInterest().getValue() > MylarContextManager.getScalingFactors().getInteresting();
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.log(t, "interest filter failed on viewer: " + viewer.getClass());
		}
		return false;
	}
	
	protected boolean isImplicitlyUninteresting(Object element, IMylarStructureBridge bridge) {
		if (element instanceof ITask) {
			ITask task = (ITask)element;
			if (task.isCompleted()) {
				return true;
			}
		}
		return false;
	}

	protected boolean isImplicitlyInteresting(Object element, IMylarStructureBridge bridge) {
		if (element instanceof ITask) {
			ITask task = (ITask)element;
			if (task.isPastReminder()) {
				return true;
			}
//			if (task.getPriority().equals(Task.PriorityLevel.P1.toString())) {
//				return true;
//			}
		}
		return false;
	}
}
