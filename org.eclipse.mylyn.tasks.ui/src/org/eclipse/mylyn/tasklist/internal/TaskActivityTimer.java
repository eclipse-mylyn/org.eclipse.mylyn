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

package org.eclipse.mylar.tasklist.internal;

import java.util.Calendar;

import org.eclipse.mylar.core.IInteractionEventListener;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.ITimerThreadListener;
import org.eclipse.mylar.core.util.TimerThread;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten 
 */
public class TaskActivityTimer implements ITimerThreadListener, IInteractionEventListener, ShellListener {

	private TimerThread timer;

	private ITask task;

	private long lastTimeout;

	public TaskActivityTimer(ITask task, int timeout) {
		this.task = task;
		timer = new TimerThread(timeout);
	}

	public void fireTimedOut() {
		long elapsed = Calendar.getInstance().getTimeInMillis() - lastTimeout;
		task.setElapsedTime(task.getElapsedTime() + elapsed);
		lastTimeout = Calendar.getInstance().getTimeInMillis();
	}

	public void interactionObserved(InteractionEvent event) {
		timer.resetTimer();
	}

	public void startTimer() {
		lastTimeout = Calendar.getInstance().getTimeInMillis();
		timer.addListener(this);
		MylarPlugin.getDefault().addInteractionListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().addShellListener(this);
		timer.start();
	}

	public void stopTimer() {
		fireTimedOut();
		timer.killTimer();
		MylarPlugin.getDefault().removeInteractionListener(this);
	}

	public void shellDeactivated(ShellEvent e) {
		fireTimedOut();
		timer.setSuspended(true);
		// windowDeactivationTime = new Date().getTime();
	}

	public void shellActivated(ShellEvent e) {
		timer.setSuspended(false);
		lastTimeout = Calendar.getInstance().getTimeInMillis();
	}

	public void shellClosed(ShellEvent e) {
		// Do nothing, deactivated will fire
	}

	public void shellDeiconified(ShellEvent e) {
		// Do nothing, wait for interaction to signal that the task is
		// continuing
	}

	public void shellIconified(ShellEvent e) {
		// Do nothing, deactivated will fire
	}
	
	public void startObserving() {
		
	}
	
	public void stopObserving() { 
		
	}
}
