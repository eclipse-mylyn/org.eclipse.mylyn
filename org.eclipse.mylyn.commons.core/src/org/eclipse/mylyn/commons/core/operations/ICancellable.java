/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
	public abstract void abort();

}
