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

package org.eclipse.mylar.monitor;

import org.eclipse.mylar.internal.context.core.util.IActivityTimerListener;
import org.eclipse.mylar.internal.context.core.util.TimerThread;

/**
 * @author Mik Kersten
 */
public class WorkbenchActivityTimer extends AbstractUserActivityTimer {
	
	private TimerThread timerThread;
	
	public WorkbenchActivityTimer(int millis) {
		timerThread = new TimerThread(millis);
	}

	@Override
	public boolean addListener(IActivityTimerListener activityListener) {
		return timerThread.addListener(activityListener);
	}

	@Override
	public void kill() {
		timerThread.kill();
	}

	@Override
	public void resetTimer() {
		timerThread.resetTimer();
	}

	@Override
	public void setTimeoutMillis(int millis) {
		timerThread.setTimeoutMillis(millis);
	}

	@Override
	public void start() {
		timerThread.start();
	}
}
