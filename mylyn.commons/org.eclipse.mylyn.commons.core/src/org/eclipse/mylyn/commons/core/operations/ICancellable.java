/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
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
 * Intended to be implemented by cancellable operations.
 *
 * @author Steffen Pingel
 * @since 3.7
 */
public interface ICancellable {

	/**
	 * Invoked on cancellation.
	 *
	 * @since 3.7
	 */
	void abort();

}
