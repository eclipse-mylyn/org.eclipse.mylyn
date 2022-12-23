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

package org.eclipse.mylyn.tasks.tests.support;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.data.ITaskDataDiff;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationParticipant;

/**
 * @author Steffen Pingel
 */
public class MockSynchronizationParticipant extends SynchronizationParticipant {

	public MockSynchronizationParticipant() {
	}

	@Override
	public void processUpdate(ITaskDataDiff diff, IProgressMonitor monitor) {
		//System.err.println(diff.getChangedAttributes());
	}

}
