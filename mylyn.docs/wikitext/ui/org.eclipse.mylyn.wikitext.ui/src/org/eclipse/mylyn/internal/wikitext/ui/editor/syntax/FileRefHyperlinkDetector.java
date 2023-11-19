/*******************************************************************************
 * Copyright (c) 2016 Simon Scholz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Simon Scholz - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class FileRefHyperlinkDetector implements IHyperlinkDetector {

	private final List<Pattern> hyperlinkPattern;

	private final IContainer container;

	/**
	 * Create a {@link FileRefHyperlinkDetector}.
	 *
	 * @param container
	 *            {@link IContainer} of the file being edited
	 * @param hyperlinkPattern
	 *            regular expression patterns with at least one group, which is supposed to contain the path to a
	 *            resource in the workspace.
	 */
	public FileRefHyperlinkDetector(IContainer container, List<String> hyperlinkPattern) {
		this.container = container;
		this.hyperlinkPattern = createHyperlinkPattern(hyperlinkPattern);
	}

	private List<Pattern> createHyperlinkPattern(Collection<String> hyperlinkPattern) {
		Builder<Pattern> hyperlinkPatternBuilder = ImmutableList.builder();
		hyperlinkPattern.forEach(pattern -> hyperlinkPatternBuilder.add(Pattern.compile(pattern)));
		return hyperlinkPatternBuilder.build();
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IDocument document = textViewer.getDocument();
		try {
			IRegion lineInfo = document.getLineInformationOfOffset(region.getOffset());
			String lineString = document.get(lineInfo.getOffset(), lineInfo.getLength());
			Optional<Matcher> fileRefPatternAtOffset = getFileRefMatchAtOffset(lineString,
					region.getOffset() - lineInfo.getOffset());
			Collection<IHyperlink> hyperlinkList = createHyperlinks(lineInfo, fileRefPatternAtOffset);
			if (hyperlinkList.isEmpty()) {
				return null;
			}

			return hyperlinkList.toArray(new IHyperlink[hyperlinkList.size()]);
		} catch (BadLocationException e) {
			// ignore
		}

		return null;
	}

	private Collection<IHyperlink> createHyperlinks(IRegion lineInfo, Optional<Matcher> fileRefPatternAtOffset) {
		Builder<IHyperlink> hyperlinks = ImmutableList.builder();

		fileRefPatternAtOffset.ifPresent(matcher -> {
			IRegion regionToMatch = new Region(lineInfo.getOffset() + matcher.start(1),
					matcher.end(1) - matcher.start(1));

			IPath path = new Path(matcher.group(1));
			IFile file = container.getFile(path);
			if (file != null && file.exists()) {
				hyperlinks.add(new EditFileHyperlink(file, regionToMatch));
			}
		});

		return hyperlinks.build();
	}

	private Optional<Matcher> getFileRefMatchAtOffset(String lineString, int offset) {
		for (Pattern pattern : hyperlinkPattern) {
			Matcher matcher = pattern.matcher(lineString);

			while (matcher.find()) {
				if (matcher.start() <= offset && matcher.end() >= offset) {
					return Optional.of(matcher);
				}
			}
		}

		return Optional.empty();
	}

}
