/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
		List<IHyperlink> result = null;
		for (final TaskRepository repository : getTaskRepositories(textViewer)) {
			final AbstractRepositoryConnectorUi connectorUi = getConnectorUi(repository);
			if (connectorUi == null) {
				continue;
			}
			final IHyperlink[][] links = new IHyperlink[1][];
			SafeRunnable.run(new ISafeRunnable() {

				public void handleException(Throwable exception) {
				}

				public void run() throws Exception {
					links[0] = connectorUi.findHyperlinks(repository, content, index, contentOffset);
				}

			});
			if (links[0] != null && links[0].length > 0) {
				if (result == null) {
					result = new ArrayList<IHyperlink>();
				}
				result.addAll(Arrays.asList(links[0]));
			}
		}
		return result;
	}

	protected AbstractRepositoryConnectorUi getConnectorUi(TaskRepository repository) {
		return TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
	}

}
