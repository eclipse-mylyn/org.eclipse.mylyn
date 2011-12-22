/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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