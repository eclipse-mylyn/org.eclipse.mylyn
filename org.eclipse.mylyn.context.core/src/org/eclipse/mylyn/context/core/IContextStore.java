/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.io.File;

import org.eclipse.core.runtime.CoreException;

/**
 * File-based store used for writing Mylyn-specific date such as the task list and task contexts (e.g.
 * workspace/.metadata/.mylyn folder).
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IContextStore {

	/**
	 * @since 3.0
	 */
	public abstract boolean hasContext(String handleIdentifier);

	/**
	 * @param zipFile
	 *            A zip file that contains a context with the specified handle identifier in its root.
	 * @return null if the import failed
	 * @throws CoreException
	 * @since 3.0
	 */
	public abstract IInteractionContext importContext(String handleIdentifier, File zipFile) throws CoreException;

	/**
	 * Creates a new context if a source context was not found.
	 * 
	 * @since 3.0
	 */
	public abstract IInteractionContext cloneContext(String sourceHandleIdentifier, String destinationHandleIdentifier);

}
