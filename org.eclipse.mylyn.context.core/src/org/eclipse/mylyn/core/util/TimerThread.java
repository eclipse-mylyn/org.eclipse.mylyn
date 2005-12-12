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
/*
 * Created on Jul 16, 2004
 */
package org.eclipse.mylar.core.util;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class TimerThread extends Thread implements Runnable {

	private static final int SECOND = 1000;

	private int sleepInterval = 5 * SECOND;

	private int timeout = 0;

	private int elapsed = 0;
	
	private List<ITimerThreadListener> listeners = new ArrayList<ITimerThreadListener>();

	private boolean suspended = false;
	
	boolean killed = false;

	/** Currently used only for testing */
	public TimerThread(int timeoutInMillis, int sleepInterval) {
		this.sleepInterval = sleepInterval;
		setTimeoutMillis(timeoutInMillis);
	}

	public TimerThread(int millis) {
		setTimeoutMillis(millis);
	}

	public boolean addListener(ITimerThreadListener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(ITimerThreadListener listener) {
		return listeners.remove(listener);
	}

	public void run() {
		try {
			while (!killed) {
				while (elapsed < timeout && !killed) {
					elapsed += sleepInterval;
					sleep(sleepInterval);
				}
				if (elapsed >= timeout && !killed) {
					if (!suspended) {
						for (ITimerThreadListener listener : listeners) listener.fireTimedOut();
					} 
					resetTimer();
				}
				sleep(sleepInterval);
			}
		} catch (InterruptedException e) {
			ErrorLogger.log(e, "timer interrupted");
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
