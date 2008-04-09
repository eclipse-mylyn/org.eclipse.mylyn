/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;

/**
 * Translates interaction with the shell/window to interaction events.
 * 
 * @author Mik Kersten
 */
public class ShellLifecycleListener implements ShellListener {

	private final IInteractionContextManager manager;

	public ShellLifecycleListener(IInteractionContextManager manager) {
		this.manager = manager;

		String productId = IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH;
		if (Platform.getProduct() != null) {
			productId = Platform.getProduct().getId();
		}

		manager.processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				IInteractionContextManager.ACTIVITY_STRUCTUREKIND_LIFECYCLE, productId,
				IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				IInteractionContextManager.ACTIVITY_DELTA_STARTED, 1f));
	}

	public void shellClosed(ShellEvent shellEvent) {
		manager.deactivateAllContexts();

		String productId = IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH;
		if (Platform.getProduct() != null) {
			productId = Platform.getProduct().getId();
		}

		manager.processActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.ATTENTION,
				IInteractionContextManager.ACTIVITY_STRUCTUREKIND_LIFECYCLE, productId,
				IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH, null,
				IInteractionContextManager.ACTIVITY_DELTA_STOPPED, 1f));
		ContextCore.getContextManager().saveActivityContext();
	}

	public void shellDeactivated(ShellEvent arg0) {
		// ignore
	}

	public void shellActivated(ShellEvent arg0) {
		// ignore
	}

	public void shellDeiconified(ShellEvent arg0) {
		// ignore
	}

	public void shellIconified(ShellEvent arg0) {
		// ignore
	}
}
