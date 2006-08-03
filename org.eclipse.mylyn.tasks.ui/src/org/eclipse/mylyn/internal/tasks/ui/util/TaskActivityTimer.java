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

package org.eclipse.mylar.internal.tasks.ui.util;

import java.util.Calendar;

import org.eclipse.mylar.context.core.IInteractionEventListener;
import org.eclipse.mylar.context.core.InteractionEvent;
import org.eclipse.mylar.internal.context.core.util.ITimerThreadListener;
import org.eclipse.mylar.internal.context.core.util.TimerThread;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class TaskActivityTimer implements ITimerThreadListener, IInteractionEventListener, IWindowListener {

	private TimerThread timer;

	private ITask task;

	private long lastActivity;

	private boolean started;

	public TaskActivityTimer(ITask task, int timeout, int sleepInterval) {
		this.task = task;
		timer = new TimerThread(timeout, sleepInterval);
		PlatformUI.getWorkbench().addWindowListener(this);
		MylarMonitorPlugin.getDefault().addInteractionListener(this);
		timer.addListener(this);
	}

	public void startTimer() {
		lastActivity = Calendar.getInstance().getTimeInMillis();
		timer.start();
		started = true;
	}

	public void stopTimer() {
		if (!timer.isSuspended()) {
			addElapsedToActivityTime();
		}
		timer.kill();
		timer.removeListener(this);
		MylarMonitorPlugin.getDefault().removeInteractionListener(this);
		PlatformUI.getWorkbench().removeWindowListener(this);
		started = false;
	}

	public void fireTimedOut() {
		suspendTiming();
	}

	public void interactionObserved(InteractionEvent event) {
		// lastActivity = Calendar.getInstance().getTimeInMillis();
		timer.resetTimer();
	}

	private void suspendTiming() {
		addElapsedToActivityTime();
		timer.setSuspended(true);
	}

	private void addElapsedToActivityTime() {
		long elapsed = Calendar.getInstance().getTimeInMillis() - lastActivity;
		task.setElapsedTime(task.getElapsedTime() + elapsed);
		lastActivity = Calendar.getInstance().getTimeInMillis();
	}

	public void startMonitoring() {

	}

	public void stopMonitoring() {

	}

	/**
	 * Public for testing
	 */
	public boolean isStarted() {
		return started;
	}

	public String toString() {
		return "timer for task: " + task.toString();
	}

	public boolean isSuspended() {
		return timer.isSuspended();
	}

	public void intervalElapsed() {
		addElapsedToActivityTime();
	}

	public void windowActivated(IWorkbenchWindow window) {
		timer.resetTimer();
		lastActivity = Calendar.getInstance().getTimeInMillis();
	}

	public void windowDeactivated(IWorkbenchWindow window) {
		suspendTiming();
	}

	public void windowClosed(IWorkbenchWindow window) {
		if (PlatformUI.getWorkbench().getWorkbenchWindowCount() == 0) {
			timer.kill();
		}
	}

	public void windowOpened(IWorkbenchWindow window) {
		// ignore
	}
}
