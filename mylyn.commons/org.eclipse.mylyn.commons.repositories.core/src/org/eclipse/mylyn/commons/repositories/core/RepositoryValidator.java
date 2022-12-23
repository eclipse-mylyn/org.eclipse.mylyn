/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Steffen Pingel
 */
public abstract class RepositoryValidator {

	private final RepositoryLocation location;

	private IStatus result;

	public RepositoryValidator(RepositoryLocation location) {
		this.location = location;
	}

	public RepositoryLocation getLocation() {
		return location;
	}

	public IStatus getResult() {
		return result;
	}

	public abstract IStatus run(IProgressMonitor monitor);

	public void setResult(IStatus result) {
		this.result = result;
	}

}
