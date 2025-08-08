/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import org.eclipse.core.runtime.IStatus;

/**
 * This class is intended to be sub-classes by clients.
 *
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class AbstractErrorReporter {

	/**
	 * @since 3.0
	 */
	public static int PRIORITY_NONE = -1;

	/**
	 * @since 3.0
	 */
	public static int PRIORITY_LOW = 10;

	/**
	 * @since 3.0
	 */
	public static int PRIORITY_DEFAULT = 100;

	/**
	 * @since 3.0
	 */
	public static int PRIORITY_HIGH = 1000;

	/**
	 * @since 3.0
	 */
	public abstract int getPriority(IStatus status);

	/**
	 * @since 3.0
	 */
	public abstract void handle(IStatus status);

}
