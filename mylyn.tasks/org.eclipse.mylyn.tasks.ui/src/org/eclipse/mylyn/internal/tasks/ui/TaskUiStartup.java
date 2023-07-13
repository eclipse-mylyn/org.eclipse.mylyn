/*******************************************************************************
 * Copyright (c) 2023 FRank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(property = EventConstants.EVENT_TOPIC + '=' + UIEvents.UILifeCycle.APP_STARTUP_COMPLETE)
public class TaskUiStartup implements EventHandler {

	@Override
	public void handleEvent(Event event) {
	}

}
