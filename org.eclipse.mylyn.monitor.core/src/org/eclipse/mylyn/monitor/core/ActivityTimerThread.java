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
/*
 * Created on Jul 16, 2004
 */
package org.eclipse.mylar.monitor.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class ActivityTimerThread extends Thread implements Runnable {

	private static final int SECOND = 1000;

	public static final int DEFAULT_SLEEP_INTERVAL = 5 * SECOND;

	private int sleepInterval;

	private int timeout = 0;

	private int elapsed = 0;

	private List<IActivityTimerListener> listeners = new ArrayList<IActivityTimerListener>();

	private boolean suspended = false;

	boolean killed = false;

	public ActivityTimerThread(int timeoutInMillis, int sleepInterval) {
		this.sleepInterval = sleepInterval;
		setTimeoutMillis(timeoutInMillis);
	}

	public ActivityTimerThread(int millis) {
		this(millis, DEFAULT_SLEEP_INTERVAL);
	}

	public boolean addListener(IActivityTimerListener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(IActivityTimerListener listener) {
		return listeners.remove(listener);
	}

	@Override
	public void run() {
		try {
			while (!killed) {
				while (elapsed < timeout && !killed) {
					elapsed += sleepInterval;
					sleep(sleepInterval);

					// if (!suspended) {
					// for (IActivityTimerListener listener : listeners) {
					// listener.intervalElapsed();
					// }
					// }
				}

				// if interaction

				if (elapsed >= timeout && !killed) {
					if (!suspended) {
						for (IActivityTimerListener listener : listeners)
							listener.fireInactive();
					}
					elapsed = 0;
				}
				sleep(sleepInterval);
			}
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public void kill() {
		killed = true;
	}

	public void resetTimer() {
		suspended = false;
		elapsed = 0;
	}

	public int getTimeoutMillis() {
		return timeout;
	}

	public void setTimeoutMillis(int timeoutInMillis) {
		this.timeout = timeoutInMillis;
		if (sleepInterval > timeoutInMillis) {
			sleepInterval = timeoutInMillis - 1;
		}
	}
}
