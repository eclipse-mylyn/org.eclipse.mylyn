/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener2;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Self-registering. De-registers itself after running.
 * 
 * API-3.0: alternate names to consider are AbstractUiBridgePlugin and AbstractFocusedUiPlugin
 * 
 * @author Mik Kersten
 * @since 2.2
 */
public abstract class AbstractContextUiPlugin extends AbstractUIPlugin implements IInteractionContextListener2 {

	private final AtomicBoolean lazyStarted = new AtomicBoolean(false);

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ContextCorePlugin.getContextManager().addListener(this);

		if (ContextCorePlugin.getContextManager().isContextActive()) {
			initLazyStart();
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		if (lazyStarted.get()) {
			lazyStop();
		}
		if (TasksUiPlugin.getTaskListManager() != null) {
			ContextCorePlugin.getContextManager().removeListener(this);
		}
	}

	/**
	 * Override with startup code.
	 * 
	 * API-3.0: IInteractionContextListener's cannot be added during this method since notifications will be lost.
	 */
	protected abstract void lazyStart(IWorkbench workbench);

	/**
	 * Override with clean-up from lazyStart.
	 */
	protected abstract void lazyStop();

	public void contextPreActivated(IInteractionContext context) {
		initLazyStart();
	}

	public void contextActivated(IInteractionContext context) {
		// ignore
	}

	private void initLazyStart() {
		if (!lazyStarted.getAndSet(true)) {
			IWorkbench workbench = PlatformUI.getWorkbench();
			try {
				lazyStart(workbench);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, super.getBundle().getSymbolicName(), IStatus.ERROR,
						"Could not lazy start context plug-in", t));
			}
			if (TasksUiPlugin.getTaskListManager() != null) {
				ContextCorePlugin.getContextManager().removeListener(this);
			}
		}
	}

	public void contextCleared(IInteractionContext context) {
		// ignore
	}

	public void contextDeactivated(IInteractionContext context) {
		// ignore
	}

	public void elementDeleted(IInteractionElement element) {
		// ignore
	}

	public void interestChanged(List<IInteractionElement> elements) {
		// ignore
	}

	public void landmarkAdded(IInteractionElement element) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement element) {
		// ignore
	}

	public void relationsChanged(IInteractionElement element) {
		// ignore	
	}

	public void elementsDeleted(List<IInteractionElement> elements) {
		// ignore	
	}
}
