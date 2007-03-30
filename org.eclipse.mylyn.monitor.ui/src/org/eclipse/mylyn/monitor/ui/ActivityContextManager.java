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

package org.eclipse.mylar.monitor.ui;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.mylar.monitor.core.IActivityTimerListener;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class ActivityContextManager implements IActivityTimerListener {

	private AbstractUserActivityTimer userActivityTimer;

	private Set<IUserAttentionListener> attentionListeners = new CopyOnWriteArraySet<IUserAttentionListener>();

	public ActivityContextManager(AbstractUserActivityTimer userActivityTimer) {
		this.userActivityTimer = userActivityTimer;
		userActivityTimer.addListener(this);
	}

	public void fireActive(long start, long end) {
		ContextCorePlugin.getContextManager().handleActivityMetaContextEvent(
				new InteractionEvent(InteractionEvent.Kind.COMMAND, MylarContextManager.ACTIVITY_STRUCTURE_KIND,
						MylarContextManager.ACTIVITY_HANDLE_ATTENTION, MylarContextManager.ACTIVITY_ORIGIN_ID, null,
						MylarContextManager.ACTIVITY_DELTA_ACTIVATED, 1f, new Date(start), new Date(end)));
		for (IUserAttentionListener attentionListener : attentionListeners) {
			attentionListener.userAttentionGained();
		}
	}

	public void fireInactive() {
		// if (!isStalled) {
		// ContextCorePlugin.getContextManager().handleActivityMetaContextEvent(
		// new InteractionEvent(InteractionEvent.Kind.COMMAND,
		// MylarContextManager.ACTIVITY_STRUCTURE_KIND,
		// MylarContextManager.ACTIVITY_HANDLE_ATTENTION,
		// MylarContextManager.ACTIVITY_ORIGIN_ID,
		// null, MylarContextManager.ACTIVITY_DELTA_DEACTIVATED, 1f));
		for (IUserAttentionListener attentionListener : attentionListeners) {
			attentionListener.userAttentionLost();
		}

		// isStalled = true;
	}

	public void start() {
		userActivityTimer.start();
	}

	public void stop() {
		userActivityTimer.kill();
	}

	public void setTimeoutMillis(int millis) {
		userActivityTimer.setTimeoutMillis(millis);
		userActivityTimer.resetTimer();
	}

	public void addListener(IUserAttentionListener listener) {
		attentionListeners.add(listener);
	}

	public void removeListener(IUserAttentionListener listener) {
		attentionListeners.remove(listener);
	}
}