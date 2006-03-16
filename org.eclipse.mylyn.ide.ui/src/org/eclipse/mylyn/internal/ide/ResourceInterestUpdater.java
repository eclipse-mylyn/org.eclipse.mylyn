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

package org.eclipse.mylar.internal.ide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ResourceInterestUpdater {

	public static final String SOURCE_ID = "org.eclipse.mylar.ide.resource.interest.updater";

	private boolean syncExec = false;

	public void addResourceToContext(final List<IResource> resources) {
		try {
			if (!resources.isEmpty()) {
				if (syncExec) {
					internalAddResourceToContext(resources);
				} else {
					final IWorkbench workbench = PlatformUI.getWorkbench();
					if (!workbench.isClosing() && !workbench.getDisplay().isDisposed()) {
						workbench.getDisplay().asyncExec(new Runnable() {
							public void run() {
								internalAddResourceToContext(resources);
							}
						});
					}
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "could not add resource to context: " + resources, false);
		}
	}

	private void internalAddResourceToContext(List<IResource> resources) {
		List<IResource> toAdd = new ArrayList<IResource>();
		for (IResource resource : resources) {
			if (acceptResource(resource)) {
				toAdd.add(resource);
			}
		}

		List<InteractionEvent> interactionEvents = new ArrayList<InteractionEvent>();
		for (IResource resource : toAdd) {
			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(resource);
			String handle = bridge.getHandleIdentifier(resource);
			if (handle != null) {
				IMylarElement element = MylarPlugin.getContextManager().getElement(handle);
				if (element != null && !element.getInterest().isInteresting()) {
					InteractionEvent interactionEvent = new InteractionEvent(InteractionEvent.Kind.SELECTION, bridge
							.getContentType(), handle, SOURCE_ID);
					interactionEvents.add(interactionEvent);
				}
			}
		} 
		MylarPlugin.getContextManager().handleInteractionEvents(interactionEvents, true);
	}

	private boolean acceptResource(IResource resource) {
		return resource.isAccessible() && !resource.isDerived() && !resource.isPhantom();
	}

	/**
	 * For testing.
	 */
	public void setSyncExec(boolean syncExec) {
		this.syncExec = syncExec;
	}
}
