/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui.editor;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.wikitext.tasks.ui.editor.MarkupTaskEditorExtension;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.AbstractTextSourceViewerConfiguration.HyperlinkDetectorDescriptorFilter;
import org.eclipse.ui.texteditor.HyperlinkDetectorDescriptor;

/**
 * @author David Green
 */
public class TracWikiMarkupTaskEditorExtension extends MarkupTaskEditorExtension<TracWikiLanguage> implements
		HyperlinkDetectorDescriptorFilter {

	public TracWikiMarkupTaskEditorExtension() {
		setMarkupLanguage(new TracWikiLanguage());
	}

	@Override
	protected void configureDefaultInternalLinkPattern(TaskRepository taskRepository, TracWikiLanguage markupLanguage) {
		String url = taskRepository.getRepositoryUrl();
		if (url != null && url.length() > 0) {
			if (!url.endsWith("/")) { //$NON-NLS-1$
				url = url + "/"; //$NON-NLS-1$
			}
			// bug 247772: set the default wiki link URL for the repository
			markupLanguage.setInternalLinkPattern(url + "wiki/{0}"); //$NON-NLS-1$

			// bug 262292: set the default server URL for non-wiki links
			markupLanguage.setServerUrl(url);
		}
	}

	@Override
	protected TaskMarkupViewerConfiguration createViewerConfiguration(TaskRepository taskRepository,
			MarkupViewer markupViewer) {
		TaskMarkupViewerConfiguration configuration = super.createViewerConfiguration(taskRepository, markupViewer);
		configuration.addHyperlinkDetectorDescriptorFilter(this);
		return configuration;
	}

	public boolean filter(HyperlinkDetectorDescriptor descriptor) {
		String id = descriptor.getId();
		if ("org.eclipse.mylyn.trac.ui.hyperlinksDetectors.Trac".equals(id)) { //$NON-NLS-1$
			return true;
		}
		return false;
	}
}
