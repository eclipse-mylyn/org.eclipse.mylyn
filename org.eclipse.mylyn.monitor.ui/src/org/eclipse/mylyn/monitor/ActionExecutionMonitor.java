/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor;

import org.eclipse.jface.action.IAction;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.IActionExecutionListener;

/**
 * @author Mik Kersten
 */
public class ActionExecutionMonitor implements IActionExecutionListener {
    	
	public void actionObserved(IAction action) {
		InteractionEvent interactionEvent = InteractionEvent.makeCommand(action.getId(), "");
		MylarPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}
}
