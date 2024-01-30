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

import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.ui.IActionExecutionListener;

/**
 * @author Mik Kersten
 */
public class ActionExecutionMonitor implements IActionExecutionListener {

	@Override
	public void actionObserved(IAction action) {
		InteractionEvent interactionEvent = InteractionEvent.makeCommand(action.getId(), ""); //$NON-NLS-1$
		MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}
}
