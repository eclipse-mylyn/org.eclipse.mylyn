/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.util;

/**
 * Interface implemented by clients who are to be notified of periodic requests to save data to disk.
 * 
 * @author Wesley Coelho
 */
public interface IBackgroundSaveListener {

	/**
	 * Called to notify the client of a PeriodicSaveTimer that a save should be performed
	 */
	public void saveRequested();
}
