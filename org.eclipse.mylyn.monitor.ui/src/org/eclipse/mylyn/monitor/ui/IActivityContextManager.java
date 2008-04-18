/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

/**
 * API-3.0: review
 * 
 * @author Rob Elves
 * @since 3.0
 */
public interface IActivityContextManager {

	public abstract void removeActivityTime(String handle, long start, long end);

	public abstract void setInactivityTimeout(int inactivityTimeout);

	public abstract int getInactivityTimeout();

}