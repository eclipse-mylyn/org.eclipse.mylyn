/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

/**
 * An operation that can be cancelled.
 *
 * @since 3.9
 */
public interface ICancellableOperation extends ICancellable {

	/**
	 * Returns <code>true</code> if this operation was requested to be cancelled.
	 */
	boolean isCanceled();

}
