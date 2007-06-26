/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.mylyn.monitor.core.ActivityTimerThread;
import org.eclipse.mylyn.monitor.core.IActivityTimerListener;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * Timer that periodically runs saveRequested() on its client as a job
 * 
 * @author Wesley Coelho
 */
public class BackgroundSaveTimer implements IActivityTimerListener {

	private final static int DEFAULT_SAVE_INTERVAL = 60 * 1000;

	private int saveInterval = DEFAULT_SAVE_INTERVAL;

	private IBackgroundSaveListener listener = null;

	private ActivityTimerThread timer = null;

// private boolean forceSyncExec = false;

	public BackgroundSaveTimer(IBackgroundSaveListener listener) {
		this.listener = listener;
		timer = new ActivityTimerThread(saveInterval);
		timer.addListener(this);
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.kill();
	}

	public void setSaveIntervalMillis(int saveIntervalMillis) {
		this.saveInterval = saveIntervalMillis;
		timer.setTimeoutMillis(saveIntervalMillis);
	}

	public int getSaveIntervalMillis() {
		return saveInterval;
	}

// /**
// * For testing
// */
// public void setForceSyncExec(boolean forceSyncExec) {
// this.forceSyncExec = forceSyncExec;
// }

	/**
	 * Called by the ActivityTimerThread Calls save in a new job
	 */
	public void fireInactive() {
		try {
// if (!forceSyncExec) {
// final SaveJob job = new SaveJob("Saving Task Data", listener);
// job.schedule();
// } else {
			listener.saveRequested();
// }
		} catch (RuntimeException e) {
			StatusHandler.log("Could not schedule save job", this);
		}
	}

// /** Job that makes the save call */
// private class SaveJob extends Job {
// private IBackgroundSaveListener listener = null;
//
// public SaveJob(String name, IBackgroundSaveListener listener) {
// super(name);
// this.listener = listener;
// }
//
// @Override
// protected IStatus run(IProgressMonitor monitor) {
// listener.saveRequested();
// return Status.OK_STATUS;
// }
// }

	public void fireActive(long start, long end) {
		// ignore
	}

}
