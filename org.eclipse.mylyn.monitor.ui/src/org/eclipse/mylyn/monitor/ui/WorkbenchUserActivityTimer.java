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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylar.internal.context.core.util.IActivityTimerListener;
import org.eclipse.mylar.internal.context.core.util.TimerThread;
import org.eclipse.mylar.monitor.core.IInteractionEventListener;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public class WorkbenchUserActivityTimer extends AbstractUserActivityTimer implements IInteractionEventListener {
	
	private TimerThread timerThread;
	
	private Set<IActivityTimerListener> listeners = new HashSet<IActivityTimerListener>();
	
	public WorkbenchUserActivityTimer(int millis) {
		timerThread = new TimerThread(millis);
	}

	public void interactionObserved(InteractionEvent event) {
		for (IActivityTimerListener listener : listeners) {
			listener.fireActive();
		}
	}
	
	@Override
	public boolean addListener(IActivityTimerListener activityListener) {
		listeners.add(activityListener);
		return timerThread.addListener(activityListener);
	}

	@Override
	public void start() {
		timerThread.start();
		MylarMonitorUiPlugin.getDefault().addInteractionListener(this);
	}
	
	@Override
	public void kill() {
		timerThread.kill();
		MylarMonitorUiPlugin.getDefault().removeInteractionListener(this);
	}

	@Override
	public void resetTimer() {
		timerThread.resetTimer();
	}

	@Override
	public void setTimeoutMillis(int millis) {
		timerThread.setTimeoutMillis(millis);
	}

	public void startMonitoring() {
		// ignore
	}

	public void stopMonitoring() {
		// ignore
	}
}
