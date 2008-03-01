/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * Notified of task activity changes. Provides extended activity support.
 * 
 * @author Shawn Minto
 * @since 2.3
 */
public interface ITaskActivityListener2 extends ITaskActivityListener {

	public abstract void preTaskActivated(AbstractTask task);

	public abstract void preTaskDeactivated(AbstractTask task);

}
