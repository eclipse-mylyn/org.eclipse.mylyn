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

package org.eclipse.mylyn.internal.context.core;

import java.io.File;

import org.eclipse.mylyn.context.core.IContextStore;

/**
 * Notified of events where {@link IContextStore} is moved.
 * 
 * @author Mik Kersten
 */
public interface IContextStoreListener {

	/**
	 * @since 3.0
	 */
	void contextStoreMoved(File newDirectory);

}
