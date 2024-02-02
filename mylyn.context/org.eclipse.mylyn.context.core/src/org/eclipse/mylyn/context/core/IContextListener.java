/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

/**
 * Interface to be notified of context change events.
 * 
 * @since 3.9
 * @author Sebastian Schmidt
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IContextListener {

	/**
	 * Context state changed
	 * 
	 * @param ContextChangeEvent
	 *            event containing the change details
	 */
	void contextChanged(ContextChangeEvent event);

}
