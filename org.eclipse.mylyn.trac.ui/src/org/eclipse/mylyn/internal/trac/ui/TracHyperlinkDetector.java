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

package org.eclipse.mylyn.internal.trac.ui;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracHyperlinkDetector extends AbstractHyperlinkDetector {

	public TracHyperlinkDetector() {
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		TaskRepository taskRepository = (TaskRepository) getAdapter(TaskRepository.class);
		if (taskRepository != null && TracCorePlugin.CONNECTOR_KIND.equals(taskRepository.getConnectorKind())) {
			IDocument document = textViewer.getDocument();
			if (document == null) {
				return null;
			}

			IRegion lineInfo;
			String line;
			try {
				lineInfo = document.getLineInformationOfOffset(region.getOffset());
				line = document.get(lineInfo.getOffset(), lineInfo.getLength());
			} catch (BadLocationException ex) {
				return null;
			}

			return TracHyperlinkUtil.findTracHyperlinks(taskRepository, line,
					region.getOffset() - lineInfo.getOffset(), lineInfo.getOffset());
		}
		return null;
	}

}
