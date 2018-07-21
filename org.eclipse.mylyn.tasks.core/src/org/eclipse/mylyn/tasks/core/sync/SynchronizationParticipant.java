/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.data.ITaskDataDiff;

/**
 * @author Steffen Pingel
 * @since 3.5
 */
public abstract class SynchronizationParticipant {

	private String id;

	public SynchronizationParticipant() {
	}

	public final String getId() {
		return id;
	}

	public abstract void processUpdate(ITaskDataDiff diff, IProgressMonitor monitor);

	public final void setId(String id) {
		Assert.isNotNull(id);
		Assert.isLegal(this.id == null);
		this.id = id;
	}

}
