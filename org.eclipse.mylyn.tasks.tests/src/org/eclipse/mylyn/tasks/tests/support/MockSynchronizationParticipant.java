/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.support;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.data.ITaskAttributeDiff;
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
		if (diff.getChangedAttributes().size() == 1) {
			ITaskAttributeDiff attributeDiff = diff.getChangedAttributes().iterator().next();
			if ("version".equals(attributeDiff.getAttributeId())) {
				diff.setHasChanged(false);
			}
		}
	}

}
