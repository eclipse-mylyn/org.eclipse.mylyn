/*******************************************************************************
 * Copyright (c) 2015 Vaughan Hilts and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Vaughan Hilts - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * This class provides a way to filter out review artifacts, since we do not want to see them in the list. As an
 * important note, this filter is not intended to have an action button on the UI, as the way it filters is ultimately
 * handled on a per repository basis.
 *
 * @author Vaughan Hilts
 */
public class TaskReviewArtifactFilter extends AbstractTaskListFilter {
	@Override
	public boolean select(Object parent, Object element) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			String artifactStateAttribute = task.getAttribute(ITasksCoreConstants.ATTRIBUTE_ARTIFACT);
			return !Boolean.parseBoolean(artifactStateAttribute);
		} else {
			return true;
		}
	}
}