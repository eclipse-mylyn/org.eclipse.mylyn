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

import org.eclipse.mylar.monitor.core.IActivityTimerListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class WorkbenchUserActivityTimer extends AbstractUserActivityTimer {

	private long lastEventTime = -1;

	private long startTime = -1;

	private int timeout;

	private Listener idleListener;

	private Display display;

	private Object eventTimeLock = new Object();

	private Object startTimeLock = new Object();

	private int tick = 30 * 1000;

	private int shortTick = 10 * 1000;

	private boolean wasTimedOut = true;

	public WorkbenchUserActivityTimer(int millis) {
		this.timeout = millis;
	}

	@Override
	public void addListener(IActivityTimerListener activityListener) {
		super.addListener(activityListener);
	}

	@Override
	public void start() {
		display = MylarMonitorUiPlugin.getDefault().getWorkbench().getDisplay();
		idleListener = new Listener() {
			public void handleEvent(Event event) {
				setLastEventTime(System.currentTimeMillis());
			}
		};
		display.addFilter(SWT.KeyUp, idleListener);
		display.addFilter(SWT.MouseUp, idleListener);

		display.timerExec(shortTick, new Runnable() {

			public void run() {
				if (!display.isDisposed() && !MylarMonitorUiPlugin.getDefault().getWorkbench().isClosing()) {

					long localLastEventTime = getLastEventTime();
					long localStartTime = getStartTime();
					long currentTime = System.currentTimeMillis();
					if ((currentTime - localLastEventTime) >= timeout) {
						if (wasTimedOut == false) {
							// timed out
							wasTimedOut = true;
						}
						display.timerExec(shortTick, this);
						return;
					} else {
						if (wasTimedOut) {
							wasTimedOut = false;
							// back...
							setStartTime(currentTime);
						} else {
							fireActive(localStartTime, currentTime);
							setStartTime(currentTime);
						}
						display.timerExec(tick, this);
					}

				}
			}
		});
	}

	@Override
	public void kill() {
		if (display != null && !display.isDisposed()) {
			display.removeFilter(SWT.KeyUp, idleListener);
			display.removeFilter(SWT.MouseUp, idleListener);
		}
	}

	@Override
	public void resetTimer() {
	}

	@Override
	public void setTimeoutMillis(int millis) {
		this.timeout = millis;
	}

	public long getLastEventTime() {
		synchronized (eventTimeLock) {
			return lastEventTime;
		}
	}

	public void setLastEventTime(long lastEventTime) {
		synchronized (eventTimeLock) {
			this.lastEventTime = lastEventTime;
		}
	}

	public long getStartTime() {
		synchronized (startTimeLock) {
			return startTime;
		}
	}

	public void setStartTime(long startTime) {
		synchronized (startTimeLock) {
			this.startTime = startTime;
		}
	}

}
