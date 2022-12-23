/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.commons.activity.ui;

/**
 * Notified of user activity and inactivity events.
 * 
 * @author Mik Kersten
 * @since 3.7
 */
public abstract class UserActivityListener {

	/**
	 * Invoked when the user becomes active.
	 * 
	 * @since 3.7
	 */
	public abstract void userAttentionGained();

	/**
	 * Invoked when the user becomes inactive.
	 * 
	 * @since 3.7
	 */
	public abstract void userAttentionLost();

	/**
	 * Invoked when the user activity is recorded over a period of time.
	 * 
	 * @param start
	 *            time in milliseconds when user activity started
	 * @param end
	 *            time in milliseconds when user activity ended
	 * @since 3.7
	 */
	public abstract void userActive(long start, long end);

}
