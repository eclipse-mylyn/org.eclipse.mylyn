/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.sync;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class SubmitJobListener {

	/**
	 * @since 3.0
	 */
	public abstract void taskSubmitted(SubmitJobEvent event, IProgressMonitor monitor) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract void taskSynchronized(SubmitJobEvent event, IProgressMonitor monitor) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract void done(SubmitJobEvent event);

}
