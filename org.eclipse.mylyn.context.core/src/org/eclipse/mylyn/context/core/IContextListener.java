/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
	public void contextChanged(ContextChangeEvent event);

}
