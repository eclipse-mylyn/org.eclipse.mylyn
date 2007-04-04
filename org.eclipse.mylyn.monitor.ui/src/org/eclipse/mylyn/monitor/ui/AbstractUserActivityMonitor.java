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

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractUserActivityMonitor {

	private long lastEventTimeStamp = -1;

	public long getLastInteractionTime() {
		synchronized (this) {
			return lastEventTimeStamp;
		}
	}

	public void setLastEventTime(long lastEventTime) {
		synchronized (this) {			
			lastEventTimeStamp = lastEventTime;
			System.err.println(">>> "+lastEventTimeStamp);
		}
	}

	public abstract void start();

	public abstract void stop();
}
