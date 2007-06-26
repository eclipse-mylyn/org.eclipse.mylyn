/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.IActionExecutionListener;
import org.eclipse.mylyn.monitor.ui.MonitorUiPlugin;

/**
 * @author Mik Kersten
 */
public class ActionExecutionMonitor implements IActionExecutionListener {

	public void actionObserved(IAction action) {
		InteractionEvent interactionEvent = InteractionEvent.makeCommand(action.getId(), "");
		MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}
}
