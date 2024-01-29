/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.AbstractErrorReporter;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ErrorLogStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class TasksBugsPlugin extends AbstractUIPlugin {

	public static class BugReporter extends AbstractErrorReporter {

		@Override
		public int getPriority(IStatus status) {
			if (status instanceof ErrorLogStatus) {
				return PRIORITY_DEFAULT;
			}
			return PRIORITY_NONE;
			//return getTaskErrorReporter().getPriority(status);
		}

		@Override
		public void handle(final IStatus status) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				Display display = workbench.getDisplay();
				if (display != null && !display.isDisposed()) {
					display.asyncExec(new Runnable() {
						public void run() {
							getTaskErrorReporter().handle(status);
						}
					});
				}
			}
		}
	}

	public static final String ID_PLUGIN = "org.eclipse.mylyn.tasks.bugs"; //$NON-NLS-1$

	private static TasksBugsPlugin INSTANCE;

	private static TaskErrorReporter taskErrorReporter;

	public static TasksBugsPlugin getDefault() {
		return INSTANCE;
	}

	public static synchronized TaskErrorReporter getTaskErrorReporter() {
		if (taskErrorReporter == null) {
			taskErrorReporter = new TaskErrorReporter();
		}
		return taskErrorReporter;
	}

	public TasksBugsPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		INSTANCE = null;
		super.stop(context);
	}

}
