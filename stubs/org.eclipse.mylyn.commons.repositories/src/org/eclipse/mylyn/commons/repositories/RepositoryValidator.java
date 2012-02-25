/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories;

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
