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

import java.util.Date;

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
 * A Task uses this class to determine when it is active
 * based on a timer that fires a timeout when there has been
 * no activity and a shell listener that detects when the
 * window has been deactivated.
 * 
 * @author Shawn Minto
 * @author Wesley Coelho (Added correction for PC sleep/hibernation errors)
 */
public class TaskTimer implements ITimerThreadListener, IInteractionEventListener, ShellListener {

	/** Amount of time for which discrepencies between timer and timestamp values will be ignored */
	private final static long SLOP_FACTOR_MILLIS = 1000 * 30; //30 seconds
	
	private TimerThread timer;
	private ITask task;
	private boolean isTaskStalled = false;
	private long windowDeactivationTime = 0;
	
	public TaskTimer(ITask task){
		this.task = task;
		timer = new TimerThread(MylarPlugin.getContextManager().getActivityTimeoutSeconds());
		timer.addListener(this);
		timer.start();
		MylarPlugin.getDefault().addInteractionListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().addShellListener(this);
	}
	
	/** Called by the timer when the user has been idle */
	public void fireTimedOut() {
		task.setActive(task.isActive(), true);
		isTaskStalled = true;
	}
	
	public void interactionObserved(InteractionEvent event) {
		timer.resetTimer();	
		
		if(isTaskStalled){
			task.setActive(task.isActive(), false);
		}
		isTaskStalled = false;
	} 
	
	public void start() {} 

	public void stopTimer() {
		timer.killThread();
		MylarPlugin.getDefault().removeInteractionListener(this);
	}

	public void stop() {}
	
	public void shellDeactivated(ShellEvent e) {
		windowDeactivationTime = new Date().getTime();
	}
	
	/**
	 * Check for and correct PC Sleep/Hibernation error
	 */
	public void shellActivated(ShellEvent e) {
		if (!isTaskStalled){
			long timeDifference = new Date().getTime() - windowDeactivationTime;
			if (timeDifference > TaskListManager.INACTIVITY_TIME_MILLIS + SLOP_FACTOR_MILLIS){
				long newTime = task.getElapsedTimeLong() - timeDifference;
				task.setElapsedTime("" + newTime);
			}	
		}
	}
	
	public void shellClosed(ShellEvent e) {
		//Do nothing, deactivated will fire
	}

	public void shellDeiconified(ShellEvent e) {
		//Do nothing, wait for interaction to signal that the task is continuing
	}

	public void shellIconified(ShellEvent e) {
		//Do nothing, deactivated will fire
	}
}
