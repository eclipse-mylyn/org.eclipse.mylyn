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

package org.eclipse.mylyn.internal.commons.activity.ui;

public interface IUserActivityManagerCallback {

	public abstract void inactive();

	public abstract void active();

	public abstract void addMonitoredActivityTime(long localStartTime, long currentTime);

	public abstract long getLastEventTime();

}
