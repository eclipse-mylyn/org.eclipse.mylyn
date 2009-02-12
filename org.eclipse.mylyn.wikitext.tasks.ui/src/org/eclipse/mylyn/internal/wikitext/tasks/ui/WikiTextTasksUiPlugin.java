/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author David Green
 */
public class WikiTextTasksUiPlugin extends AbstractUIPlugin {

	public static final String FONT_REGISTRY_KEY_DEFAULT_FONT = "org.eclipse.mylyn.wikitext.tasks.ui.defaultFont"; //$NON-NLS-1$

	public static final String FONT_REGISTRY_KEY_MONOSPACE_FONT = "org.eclipse.mylyn.wikitext.tasks.ui.monospaceFont"; //$NON-NLS-1$

	/**
	 * the preference key for active folding
	 */
	public static final String PREF_ACTIVE_FOLDING_ENABLED = "org.eclipse.mylyn.context.ui.editor.folding.enabled"; //$NON-NLS-1$

	private static WikiTextTasksUiPlugin plugin;

	private WikiTextUserInteractionMonitor userInteractionMonitor;

	private ActiveFoldingEditorTracker activeFoldingEditorTracker;

	public WikiTextTasksUiPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (userInteractionMonitor != null) {
			MonitorUi.getSelectionMonitors().remove(userInteractionMonitor);
			userInteractionMonitor = null;
		}
		if (activeFoldingEditorTracker != null) {
			activeFoldingEditorTracker.dispose(PlatformUI.getWorkbench());
			activeFoldingEditorTracker = null;
		}
		plugin = null;
		super.stop(context);
	}

	public static WikiTextTasksUiPlugin getDefault() {
		return plugin;
	}

	public void log(Throwable ce) {
		if (ce instanceof CoreException) {
			getLog().log(((CoreException) ce).getStatus());
		} else {
			log(IStatus.ERROR, ce.getMessage(), ce);
		}
	}

	public void log(int severity, String message, Throwable exception) {
		if (message == null) {
			message = ""; //$NON-NLS-1$
		}
		ILog log = getLog();
		IStatus status = null;
		if (exception instanceof CoreException) {
			status = ((CoreException) exception).getStatus();
		}
		if (status == null) {
			status = new Status(severity, getPluginId(), severity, message, exception);
		}
		log.log(status);
	}

	public String getPluginId() {
		return getBundle().getSymbolicName();
	}

	public IStatus createStatus(int statusCode, Throwable exception) {
		return createStatus(null, statusCode, exception);
	}

	public IStatus createStatus(String message, int statusCode, Throwable exception) {
		if (message == null && exception != null) {
			message = exception.getClass().getName() + ": " + exception.getMessage(); //$NON-NLS-1$
		}
		Status status = new Status(statusCode, getPluginId(), statusCode, message, exception);
		return status;
	}

	void contextUiStartup() {
		userInteractionMonitor = new WikiTextUserInteractionMonitor();
		MonitorUi.getSelectionMonitors().add(userInteractionMonitor);

		activeFoldingEditorTracker = new ActiveFoldingEditorTracker(PlatformUI.getWorkbench());
	}

}
