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

package org.eclipse.mylar.monitor;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.InteractionEvent;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.mylar.internal.context.core.util.IActivityTimerListener;

/**
 * @author Mik Kersten
 */
class ActivityContextManager implements IActivityTimerListener {

	private AbstractUserActivityTimer userActivityTimer;

	private boolean isStalled;

	public ActivityContextManager(AbstractUserActivityTimer userActivityTimer) {
		this.userActivityTimer = userActivityTimer;
		userActivityTimer.addListener(this);
	}

	public void start() {
		userActivityTimer.start();
	}
	
	public void stop() {
		userActivityTimer.kill();
	}
	
	public void fireActive() {
		userActivityTimer.resetTimer();
		if (isStalled) {
			ContextCorePlugin.getContextManager().handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					 MylarContextManager.ACTIVITY_STRUCTURE_KIND,  MylarContextManager.ACTIVITY_HANDLE_ATTENTION, MylarContextManager.ACTIVITY_ORIGIN_ID, null,
					MylarContextManager.ACTIVITY_DELTA_ACTIVATED, 1f));
		}
		isStalled = false;
	}

	public void fireInactive() {
		if (!isStalled) {
			ContextCorePlugin.getContextManager().handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					 MylarContextManager.ACTIVITY_STRUCTURE_KIND,  MylarContextManager.ACTIVITY_HANDLE_ATTENTION, MylarContextManager.ACTIVITY_ORIGIN_ID, null,
					MylarContextManager.ACTIVITY_DELTA_DEACTIVATED, 1f));
		}
		isStalled = true;
	}

	public void setTimeoutMillis(int millis) {
		userActivityTimer.setTimeoutMillis(millis);
		userActivityTimer.resetTimer();
	}
}