/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.core;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Steffen Pingel
 * @since 2.3
 */
public abstract class AbstractErrorReporter {

	public static int PRIORITY_NONE = -1;

	public static int PRIORITY_LOW = 10;

	public static int PRIORITY_DEFAULT = 100;

	public static int PRIORITY_HIGH = 1000;

	public abstract int getPriority(IStatus status);

	public abstract void handle(IStatus status);

}
