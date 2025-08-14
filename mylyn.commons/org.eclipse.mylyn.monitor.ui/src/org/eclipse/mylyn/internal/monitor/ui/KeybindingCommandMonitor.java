/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.AbstractCommandMonitor;

/**
 * @author Mik Kersten
 */
public class KeybindingCommandMonitor extends AbstractCommandMonitor {

	public static final String COMMAND_INVOKED = "keybinding"; //$NON-NLS-1$

	@Override
	protected void handleCommandExecution(String commandId, ExecutionEvent event) {
		InteractionEvent interactionEvent = InteractionEvent.makeCommand(commandId, COMMAND_INVOKED);
		MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

}
