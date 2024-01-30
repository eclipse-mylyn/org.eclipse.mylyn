/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.commons.activity.ui.spi;

/**
 * Extend to monitor periods of user activity and inactivity.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 3.7
 */
public abstract class AbstractUserActivityMonitor {

	private long lastEventTimeStamp = -1;

	/**
	 * Returns the priority of the monitor. A lower priority means that the monitor is preferred over other monitors. The priority of the
	 * default monitor is <code>0</code>.
	 * 
	 * @since 3.7
	 */
	public abstract int getPriority();

	/**
	 * @since 3.7
	 */
	public long getLastInteractionTime() {
		synchronized (this) {
			return lastEventTimeStamp;
		}
	}

	/**
	 * @since 3.7
	 */
	public void setLastEventTime(long lastEventTime) {
		synchronized (this) {
			lastEventTimeStamp = lastEventTime;
		}
	}

	/**
	 * @since 3.7
	 */
	public abstract void start();

	/**
	 * @since 3.7
	 */
	public abstract void stop();

	/**
	 * @return false if monitor unable to run (i.e. startup failures of any kind)
	 * @since 3.7
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @since 3.7
	 */
	public String getOriginId() {
		return null;
	}

}
