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

/**
 * @author Mik Kersten
 */
public abstract class AbstractUserActivityTimer {

	/**
	 * The listener needs to be notified of timed user activity and inactivity
	 */
	public abstract boolean addListener(IActivityTimerListener activityListener);

	public abstract void resetTimer();

	public abstract void kill();

	public abstract void start();

	public abstract void setTimeoutMillis(int millis);

}
