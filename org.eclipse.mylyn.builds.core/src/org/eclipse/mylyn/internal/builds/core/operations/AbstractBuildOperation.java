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

package org.eclipse.mylyn.internal.builds.core.operations;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.builds.core.util.BuildsConstants;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractBuildOperation extends Job {

	IStatus status;

	public AbstractBuildOperation(String name) {
		super(name);
	}

	@Override
	public boolean belongsTo(Object family) {
		return family == BuildsConstants.JOB_FAMILY;
	}

	public IStatus getStatus() {
		return status;
	}

	protected void setStatus(IStatus status) {
		this.status = status;
	}

}
