/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener2;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetUpdater;

/**
 * TODO: consider removing
 * 
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class ContextWorkingSetManager implements IWorkingSetUpdater, IInteractionContextListener2 {

	private static ContextWorkingSetManager INSTANCE = new ContextWorkingSetManager();

	private List<ContextWorkingSetManager> workingSetUpdaters = null;

	public void addWorkingSetManager(ContextWorkingSetManager updater) {
		if (workingSetUpdaters == null) {
			workingSetUpdaters = new ArrayList<ContextWorkingSetManager>();
		}
		workingSetUpdaters.add(updater);
		ContextCore.getContextManager().addListener(updater);
	}

	public ContextWorkingSetManager getWorkingSetUpdater() {
		if (workingSetUpdaters == null) {
			return null;
		} else {
			return workingSetUpdaters.get(0);
		}
	}

	/** Should only ever have 1 working set */
	private final List<IWorkingSet> workingSets = new ArrayList<IWorkingSet>();

	public void add(IWorkingSet workingSet) {
		workingSets.add(workingSet);
	}

	public boolean remove(IWorkingSet workingSet) {
		return workingSets.remove(workingSet);

	}

	public boolean contains(IWorkingSet workingSet) {
		return workingSets.contains(workingSet);
	}

	public void dispose() {
		// nothing to do here
	}

	public void contextActivated(IInteractionContext context) {
		updateWorkingSet();
	}

	public void contextDeactivated(IInteractionContext context) {
		updateWorkingSet();
	}

	public void contextCleared(IInteractionContext context) {
		updateWorkingSet();
	}

	public void interestChanged(List<IInteractionElement> nodes) {
		updateWorkingSet();

	}

	public void elementDeleted(IInteractionElement node) {
		updateWorkingSet();
	}

	public void elementsDeleted(List<IInteractionElement> elements) {
		updateWorkingSet();
	}

	public void landmarkAdded(IInteractionElement node) {
		updateWorkingSet();

	}

	public void landmarkRemoved(IInteractionElement node) {
		updateWorkingSet();

	}

	public void relationsChanged(IInteractionElement node) {
		// don't care about this relationship

	}

	private void updateWorkingSet() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (workingSets.size() <= 0) {
					return;
				}
				IWorkingSet set = workingSets.get(0);
				set.setElements(new IAdaptable[] {});
				List<IAdaptable> elements = new ArrayList<IAdaptable>();
				getElementsFromTaskscape(elements);
				set.setElements(elements.toArray(new IAdaptable[elements.size()]));
			}
		});
	}

	public static void getElementsFromTaskscape(List<IAdaptable> elements) {
		for (IInteractionElement node : ContextCore.getContextManager().getInterestingDocuments()) {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
					node.getContentType());

			// HACK comparing extension to string
			// No need to add bugzilla resources to the taskscape
			// search...really slow and eclipese doesn't know about them
			if (bridge.getContentType().equals("bugzilla")) {
				continue;
			}

			Object o = bridge.getObjectForHandle(node.getHandleIdentifier());
			if (o instanceof IAdaptable) {
				elements.add((IAdaptable) o);
			}

		}
	}

	public IWorkingSet getWorkingSet() {
		return workingSets.get(0);
	}

	public static ContextWorkingSetManager getDefault() {
		return INSTANCE;
	}

	public void contextPreActivated(IInteractionContext context) {
		// ignore

	}

}
