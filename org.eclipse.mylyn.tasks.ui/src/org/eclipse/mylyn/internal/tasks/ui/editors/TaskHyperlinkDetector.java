/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;

/**
 * Delegates to {@link AbstractRepositoryConnectorUi} for detecting hyperlinks.
 * 
 * @author Steffen Pingel
 */
public class TaskHyperlinkDetector extends AbstractTaskHyperlinkDetector {

	@Override
	protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, final String content, final int index,
			final int contentOffset) {
		List<IHyperlink> result = new ArrayList<IHyperlink>();
		for (final TaskRepository repository : getTaskRepositories(textViewer)) {
			final IHyperlink[] links = detectHyperlinks(repository, content, index, contentOffset);
			if (links != null && links.length > 0) {
				result.addAll(Arrays.asList(links));
			}
		}
		if (result.isEmpty()) {
			return null;
		}
		return result;
	}

	protected IHyperlink[] detectHyperlinks(final TaskRepository repository, final String content, final int index,
			final int contentOffset) {
		final AbstractRepositoryConnectorUi connectorUi = getConnectorUi(repository);
		if (connectorUi == null) {
			return null;
		}
		final IHyperlink[][] links = new IHyperlink[1][];
		SafeRunnable.run(new ISafeRunnable() {
			public void handleException(Throwable exception) {
			}

			public void run() throws Exception {
				final ITask task = (ITask) getAdapter(ITask.class);
				links[0] = connectorUi.findHyperlinks(repository, task, content, index, contentOffset);
			}
		});
		return links[0];
	}

	protected AbstractRepositoryConnectorUi getConnectorUi(TaskRepository repository) {
		return TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
	}

}
