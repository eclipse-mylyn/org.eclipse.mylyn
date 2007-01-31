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

import java.util.List;

import org.eclipse.mylar.context.core.IInteractionEventListener;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.InteractionEvent;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.mylar.internal.context.core.util.ITimerThreadListener;
import org.eclipse.mylar.internal.context.core.util.TimerThread;

/**
 * @author Mik Kersten
 */
class ActivityListener implements ITimerThreadListener, IInteractionEventListener, IMylarContextListener {

	private TimerThread timer;

	private int sleepPeriod = 60000;

	private boolean isStalled;

	public ActivityListener(int millis) {
		timer = new TimerThread(millis);
		timer.addListener(this);
		timer.start();
		sleepPeriod = millis;
		MylarMonitorPlugin.getDefault().addInteractionListener(this);
	}

	public void fireTimedOut() {
		if (!isStalled) {
			ContextCorePlugin.getContextManager().handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					 MylarContextManager.ACTIVITY_STRUCTURE_KIND,  MylarContextManager.ACTIVITY_HANDLE_ATTENTION, MylarContextManager.ACTIVITY_ORIGIN_ID, null,
					MylarContextManager.ACTIVITY_DELTA_DEACTIVATED, 1f));
		}
		isStalled = true;
	}

	public void intervalElapsed() {
		// ignore

	}

	public void interactionObserved(InteractionEvent event) {
		timer.resetTimer();
		if (isStalled) {
			ContextCorePlugin.getContextManager().handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND,
					 MylarContextManager.ACTIVITY_STRUCTURE_KIND,  MylarContextManager.ACTIVITY_HANDLE_ATTENTION, MylarContextManager.ACTIVITY_ORIGIN_ID, null,
					MylarContextManager.ACTIVITY_DELTA_ACTIVATED, 1f));
		}
		isStalled = false;
	}

	public void startMonitoring() {
	}

	public void stopTimer() {
		timer.kill();
		MylarMonitorPlugin.getDefault().removeInteractionListener(this);
	}

	public void stopMonitoring() {
	}

	public void setTimeout(int millis) {
		timer.kill();
		sleepPeriod = millis;
		timer = new TimerThread(millis);
		timer.addListener(this);
		timer.start();
	}

	public void contextActivated(IMylarContext context) {
		interactionObserved(null);
		timer.kill();
		timer = new TimerThread(sleepPeriod);
		timer.addListener(this);
		timer.start();
	}

	public void contextDeactivated(IMylarContext context) {
		interactionObserved(null);
		timer.kill();
	}

	public void presentationSettingsChanging(UpdateKind kind) {
		// ignore
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// ignore
	}

	public void interestChanged(List<IMylarElement> elements) {
		// ignore
	}

	public void elementDeleted(IMylarElement element) {
		// ignore
	}

	public void landmarkAdded(IMylarElement element) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement element) {
		// ignore
	}

	public void relationsChanged(IMylarElement element) {
		// ignore
	}
}