/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * Triggers the job that refreshes the Builds view.
 * 
 * @author Steffen Pingel
 */
public class BuildsStartup implements IStartup {

	public void earlyStartup() {
		UIJob job = new UIJob("Initializing Builds View") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (activeWorkbenchWindow != null) {
						IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
						if (activePage != null) {
							for (IViewReference view : activePage.getViewReferences()) {
								if (view.getId().equals(BuildsUiConstants.ID_VIEW)) {
									// ensure that build view decoration is accurate
									activePage.showView(BuildsUiPlugin.ID_PLUGIN, null, IWorkbenchPage.VIEW_CREATE);
								}
							}
						}
					}
					// FIXME trigger refresh job
				} catch (PartInitException e) {
					StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
							"Unexpected error during initialization of Builds View", e)); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

}
