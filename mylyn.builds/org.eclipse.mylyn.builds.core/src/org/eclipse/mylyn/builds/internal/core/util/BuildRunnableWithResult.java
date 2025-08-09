/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.util;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Steffen Pingel
 */
public abstract class BuildRunnableWithResult<T> {

	public boolean handleException(Throwable exception) throws CoreException {
		return false;
	}

	public abstract T run() throws CoreException;

}
