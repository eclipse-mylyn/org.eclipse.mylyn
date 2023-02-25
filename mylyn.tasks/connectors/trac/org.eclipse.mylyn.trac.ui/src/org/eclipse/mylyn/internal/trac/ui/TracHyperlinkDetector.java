/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;

/**
 * @author Steffen Pingel
 */
public class TracHyperlinkDetector extends AbstractTaskHyperlinkDetector {

	public TracHyperlinkDetector() {
	}

	@Override
	protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int index, int contentOffset) {
		TaskRepository taskRepository = getTaskRepository(textViewer);
		if (taskRepository != null && TracCorePlugin.CONNECTOR_KIND.equals(taskRepository.getConnectorKind())) {
			return TracHyperlinkUtil.findTracHyperlinks(taskRepository, content, index, contentOffset);
		}
		return null;
	}

}
