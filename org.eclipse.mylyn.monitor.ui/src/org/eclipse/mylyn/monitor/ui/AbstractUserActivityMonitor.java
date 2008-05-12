/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContextManager;

/**
 * Extend to monitor periods of user activity and inactivity.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since 2.0
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
		}
	}

	public abstract void start();

	public abstract void stop();

	/**
	 * @return false if monitor unable to run (i.e. startup failures of any kind)
	 */
	public boolean isEnabled() {
		return true;
	}

	public String getOriginId() {
		return IInteractionContextManager.ACTIVITY_ORIGINID_WORKBENCH;
	}

	public String getStructureKind() {
		return IInteractionContextManager.ACTIVITY_STRUCTUREKIND_TIMING;
	}

	public String getStructureHandle() {
		if (ContextCore.getContextManager().getActiveContext().getHandleIdentifier() != null) {
			return ContextCore.getContextManager().getActiveContext().getHandleIdentifier();
		} else {
			return IInteractionContextManager.ACTIVITY_DELTA_ADDED;
		}
	}

}
