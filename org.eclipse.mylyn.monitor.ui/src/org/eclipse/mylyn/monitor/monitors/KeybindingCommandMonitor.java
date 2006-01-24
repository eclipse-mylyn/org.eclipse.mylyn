/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor.monitors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylar.core.AbstractCommandMonitor;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class KeybindingCommandMonitor extends AbstractCommandMonitor {

	public static final String COMMAND_INVOKED = "keybinding";

	@Override
	protected void handleCommandExecution(String commandId, ExecutionEvent event) {
		InteractionEvent interactionEvent = InteractionEvent.makeCommand(commandId, COMMAND_INVOKED);
		MylarPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

}
