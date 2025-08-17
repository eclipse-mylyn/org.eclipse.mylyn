/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.spi.remote.ReviewsDataLocator;

/**
 * @author Miles Parker
 */
public class ReviewsUiDataLocator extends ReviewsDataLocator {

	@SuppressWarnings("restriction")
	@Override
	public IPath getSystemDataPath() {
		return new Path(TasksUiPlugin.getTaskDataManager().getDataPath());
	}

}
