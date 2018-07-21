/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.operations;

import java.util.concurrent.Callable;

/**
 * A callable that is notified on cancellation.
 * 
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class Operation<T> implements Callable<T>, ICancellable {

	/**
	 * Invoked when the operation is cancelled.
	 * 
	 * @since 3.7
	 */
	public abstract void abort();

}