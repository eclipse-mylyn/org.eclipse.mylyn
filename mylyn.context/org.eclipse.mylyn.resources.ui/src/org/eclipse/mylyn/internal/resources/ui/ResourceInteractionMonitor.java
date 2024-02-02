/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 */
public class ResourceInteractionMonitor extends AbstractUserInteractionMonitor {

	private static final String ID_SYNCHRONIZE_VIEW = "org.eclipse.team.sync.views.SynchronizeView"; //$NON-NLS-1$

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection,
			boolean contributeToContext) {
		// ignore events from the synchronize view to avoid files jumping between change sets when selected
		if (ID_SYNCHRONIZE_VIEW.equals(part.getSite().getId())) {
			return;
		}
		if (selection instanceof StructuredSelection structuredSelection) {
			//			Object selectedObject = structuredSelection.getFirstElement();
			for (Object selectedObject : structuredSelection) {
				IResource resource;
				if (selectedObject instanceof IResource) {
					resource = (IResource) selectedObject;
					super.handleElementSelection(part, resource, contributeToContext);
				}

			}
		} else if (selection instanceof TextSelection) {
			if (part instanceof EditorPart) {
				try {
					Object object = ((EditorPart) part).getEditorInput().getAdapter(IResource.class);
					if (object instanceof IFile file) {
						if (file.getFileExtension() != null
								&& !ContextCore.getContentTypes().contains(file.getFileExtension())) {
							super.handleElementEdit(part, object, contributeToContext);
						}
					}
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN,
							"Failed to resolve resource edit", t)); //$NON-NLS-1$
				}
			}
		}
	}
}
