/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ResourceInterestUpdater {

	/**
	 * In Mylyn 2.0 was: "org.eclipse.mylyn.ide.resource.interest.updater"
	 */
	public static final String SOURCE_ID = "org.eclipse.mylyn.resources.ui.interest.updater";

	private boolean syncExec = false;

	public void addResourceToContext(final Set<IResource> resources, final InteractionEvent.Kind interactionKind) {
		try {
			if (!resources.isEmpty()) {
				if (syncExec) {
					internalAddResourceToContext(resources, interactionKind);
				} else {
					final IWorkbench workbench = PlatformUI.getWorkbench();
					if (!workbench.isClosing() && !workbench.getDisplay().isDisposed()) {
						workbench.getDisplay().asyncExec(new Runnable() {
							public void run() {
								internalAddResourceToContext(resources, interactionKind);
							}
						});
					}
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN, ""
					+ "Could not add resource to context: \"" + resources + "\"", t));
		}
	}

	@SuppressWarnings("restriction")
	private void internalAddResourceToContext(Set<IResource> resources, InteractionEvent.Kind interactionKind) {
		List<InteractionEvent> interactionEvents = new ArrayList<InteractionEvent>();
		for (IResource resource : resources) {
			if (acceptResource(resource)) {
				AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(resource);
				String handle = bridge.getHandleIdentifier(resource);
				if (handle != null) {
					IInteractionElement element = ContextCore.getContextManager().getElement(handle);
					if (element != null && !element.getInterest().isInteresting()) {
						InteractionEvent interactionEvent = new InteractionEvent(interactionKind,
								bridge.getContentType(), handle, SOURCE_ID);
						interactionEvents.add(interactionEvent);
					}
				}
			}
		}
		if (InteractionEvent.Kind.SELECTION.equals(interactionKind)) {
			ContextCorePlugin.getContextManager().processInteractionEvents(interactionEvents, true);
		} else {
			ContextCorePlugin.getContextManager().processInteractionEvents(interactionEvents, false);
		}
	}

	private boolean acceptResource(IResource resource) {
		if (resource.isAccessible() && !resource.isDerived() && !resource.isPhantom()) {
			return true;
		}
		return false;
	}

	/**
	 * For testing.
	 */
	public void setSyncExec(boolean syncExec) {
		this.syncExec = syncExec;
	}
}
