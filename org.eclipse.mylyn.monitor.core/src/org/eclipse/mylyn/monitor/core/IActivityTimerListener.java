/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.core;

/**
 * Notified of user activity and inactivity events.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IActivityTimerListener {

	/**
	 * @param start
	 *            time in milliseconds
	 * @param end
	 *            time in milliseconds
	 */
	public void fireActive(long start, long end);

	public void fireInactive();

}
