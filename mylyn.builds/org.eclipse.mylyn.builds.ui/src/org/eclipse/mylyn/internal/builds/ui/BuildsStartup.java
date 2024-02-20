/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Triggers the job that refreshes the Builds view.
 *
 * @author Steffen Pingel
 */
@Component(property = EventConstants.EVENT_TOPIC + '=' + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)

public class BuildsStartup implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		UIJob job = new UIJob(Messages.BuildsStartup_initializingBuildsView) {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					if (activeWorkbenchWindow != null) {
						IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
						if (activePage != null) {
							for (IViewReference view : activePage.getViewReferences()) {
								if (view.getId().equals(BuildsUiConstants.ID_VIEW_BUILDS)) {
									// ensure that build view decoration is accurate
									activePage.showView(BuildsUiConstants.ID_VIEW_BUILDS, null,
											IWorkbenchPage.VIEW_CREATE);
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
