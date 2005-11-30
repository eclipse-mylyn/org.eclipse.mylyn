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

package org.eclipse.mylar.ide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: refactor into bridges?
 * 
 * @author Mik Kersten
 */
public class AbstractResourceInterestUpdater {

	public static final String SOURCE_ID = "org.eclipse.mylar.ide.resource.interest.updater";
	
	protected void addResourceToContext(final IResource resource) {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (resource instanceof IFile) {
					String handle = getHandleIdentifier(resource);
					if (handle != null) {
						InteractionEvent manipulationEvent = new InteractionEvent(
				                InteractionEvent.Kind.SELECTION,
				                getContentType(resource),
				                handle,
				                getSourceId());
						MylarPlugin.getContextManager().handleInteractionEvent(manipulationEvent, true);
					}
				}
			}
		});
	}
	
	protected String getHandleIdentifier(IResource resource) {
		return MylarPlugin.getDefault().getStructureBridge(resource).getHandleIdentifier(resource);
	}
	
	protected String getContentType(IResource resource) {
		return ResourceStructureBridge.CONTENT_TYPE;
	}
	
	protected String getSourceId() {
		return SOURCE_ID;
	}
	
}
