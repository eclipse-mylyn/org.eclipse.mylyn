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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.wikitext.tasks.ui.editor.MarkupTaskEditorExtension;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.ui.viewer.MarkupViewer;
import org.eclipse.mylyn.wikitext.ui.viewer.AbstractTextSourceViewerConfiguration.HyperlinkDetectorDescriptorFilter;
import org.eclipse.ui.texteditor.HyperlinkDetectorDescriptor;

/**
 * 
 * 
 * @author David Green
 */
public class TracWikiMarkupTaskEditorExtension extends MarkupTaskEditorExtension<TracWikiLanguage> implements
		HyperlinkDetectorDescriptorFilter {

	private static class TracTaskHyperlinkDetector extends AbstractTaskHyperlinkDetector {

		private final Pattern pattern = Pattern.compile("(?:(?<=[\\s\\.\\\"'?!;:\\)\\(\\{\\}\\[\\]-])|^)((?:comment:(\\d+):)?(?:#|ticket:)(\\d+))"); //$NON-NLS-1$

		public TracTaskHyperlinkDetector() {
		}

		@Override
		protected List<IHyperlink> detectHyperlinks(ITextViewer textViewer, String content, int index, int contentOffset) {
			TaskRepository taskRepository = getTaskRepository(textViewer);
			if (taskRepository != null && "trac".equals(taskRepository.getConnectorKind())) { //$NON-NLS-1$
				List<IHyperlink> hyperlinks = null;
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					if (isInRegion(index, matcher)) {
						if (hyperlinks == null) {
							hyperlinks = new ArrayList<IHyperlink>();
						}
						String taskId = matcher.group(3);
						hyperlinks.add(new TaskHyperlink(determineRegion(contentOffset, matcher), taskRepository,
								taskId));
					}
				}
				return hyperlinks;
			}
			return null;
		}

		private boolean isInRegion(int offsetInText, Matcher m) {
			return (offsetInText == -1) || (offsetInText >= m.start() && offsetInText <= m.end());
		}

		private IRegion determineRegion(int textOffset, Matcher m) {
			return new Region(textOffset + m.start(), m.end() - m.start());
		}
	}

	private final TracTaskHyperlinkDetector hyperlinkDetector = new TracTaskHyperlinkDetector();

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
	protected TaskMarkupSourceViewerConfiguration createSourceViewerConfiguration(TaskRepository taskRepository,
			SourceViewer viewer) {
		TaskMarkupSourceViewerConfiguration configuration = super.createSourceViewerConfiguration(taskRepository,
				viewer);
		configuration.addHyperlinkDetectorDescriptorFilter(this);
		configuration.addHyperlinkDetector(hyperlinkDetector);
		return configuration;
	}

	@Override
	protected TaskMarkupViewerConfiguration createViewerConfiguration(TaskRepository taskRepository,
			MarkupViewer markupViewer) {
		TaskMarkupViewerConfiguration configuration = super.createViewerConfiguration(taskRepository, markupViewer);
		configuration.addHyperlinkDetectorDescriptorFilter(this);
		configuration.addHyperlinkDetector(hyperlinkDetector);
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
