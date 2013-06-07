/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.spi.factories;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractDataLocator;

/**
 * @author Miles Parker
 */
public class UiDataLocator extends AbstractDataLocator {

	private static final String MODEL_DIR = "model"; //$NON-NLS-1$

	@SuppressWarnings("restriction")
	@Override
	public IPath getSystemPath() {
		return new Path(TasksUiPlugin.getTaskDataManager().getDataPath() + File.separator + MODEL_DIR);
	}
}
