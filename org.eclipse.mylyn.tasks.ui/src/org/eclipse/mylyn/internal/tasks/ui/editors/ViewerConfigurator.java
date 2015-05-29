/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

class ViewerConfigurator {

	static SourceViewerConfiguration configure(TaskRepository repository, ITask task, boolean spellCheck, Mode mode) {
		RepositoryTextViewerConfiguration viewerConfig = new RepositoryTextViewerConfiguration(repository, task,
				spellCheck);
		viewerConfig.setMode(mode);
		return viewerConfig;
	}
}
