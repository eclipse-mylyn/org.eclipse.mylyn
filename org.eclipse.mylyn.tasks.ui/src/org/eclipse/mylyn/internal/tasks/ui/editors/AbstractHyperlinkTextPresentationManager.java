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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextPresentationListener;
import org.eclipse.jface.text.ITextViewerExtension4;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.tasks.ui.IHighlightingHyperlink;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.swt.custom.StyleRange;

/**
 * A manager that ensures that all task hyperlinks have the appropriate text presentation. Subclasses may specify logic
 * for filtering detected hyperlinks and text decoration.
 * 
 * @author David Green
 */
public abstract class AbstractHyperlinkTextPresentationManager {

	private static class RegionComparator implements Comparator<IRegion> {

		public int compare(IRegion o1, IRegion o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1.getOffset() < o2.getOffset()) {
				return -1;
			} else if (o1.getOffset() > o2.getOffset()) {
				return 1;
			} else if (o1.getLength() < o2.getLength()) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	private class Support implements ITextPresentationListener {
		public void applyTextPresentation(TextPresentation textPresentation) {
			StyleRange[] styleRanges = computeStyleRanges(textPresentation.getCoverage());
			if (styleRanges != null && styleRanges.length > 0) {
				textPresentation.mergeStyleRanges(styleRanges);
			}
		}
	}

	private static final RegionComparator REGION_COMPARATOR = new RegionComparator();

	private IHyperlinkDetector[] hyperlinkDetectors;

	private final ITextPresentationListener textPresentationListener = new Support();

	private ISourceViewer viewer;

	public AbstractHyperlinkTextPresentationManager() {
	}

	private void addRange(List<StyleRange> ranges, int start, int end) {
		// the style range is to be merged with other styles, so only set the bits that are needed
		StyleRange styleRange = new StyleRange(start, end - start, null, null);
		decorate(styleRange);
		ranges.add(styleRange);
	}

	protected StyleRange[] computeStyleRanges(IRegion extent) {
		if (viewer == null || hyperlinkDetectors == null || viewer.getDocument() == null) {
			return null;
		}
		List<IRegion> regions = getRegions(extent);
		if (regions != null) {
			// style ranges may be adjacent but must not overlap, and they must be in order
			// of increasing offset.
			List<StyleRange> ranges = new ArrayList<StyleRange>(regions.size());
			// sort them first to ensure increasing offset
			Collections.sort(regions, REGION_COMPARATOR);
			// now merge overlapping (and adjacent) ranges
			int start = -1;
			int end = -1;
			for (int x = 0; x < regions.size(); ++x) {
				IRegion region = regions.get(x);
				if (start == -1) {
					start = region.getOffset();
					end = region.getOffset() + region.getLength();
				} else {
					if (region.getOffset() >= end) {
						addRange(ranges, start, end);
						start = region.getOffset();
						end = region.getOffset() + region.getLength();
					} else {
						end = region.getOffset() + region.getLength();
					}
				}
			}
			if (start != -1) {
				addRange(ranges, start, end);
			}
			return ranges.toArray(new StyleRange[ranges.size()]);
		}
		return null;
	}

	protected abstract void decorate(StyleRange styleRange);

	/**
	 * The hyperlink detectors that are used to detect {@link TaskHyperlink task hyperlinks}
	 */
	public IHyperlinkDetector[] getHyperlinkDetector() {
		return hyperlinkDetectors;
	}

	protected List<IRegion> getRegions(IRegion extent) {
		List<IRegion> regions = null;
		for (IHyperlinkDetector hyperlinkDetector : hyperlinkDetectors) {
			IHyperlink[] hyperlinks = hyperlinkDetector.detectHyperlinks(viewer, extent, true);
			if (hyperlinks != null && hyperlinks.length > 0) {
				for (IHyperlink hyperlink : hyperlinks) {
					if (select(hyperlink)) {
						if (regions == null) {
							regions = new ArrayList<IRegion>();
						}
						regions.add(hyperlink instanceof IHighlightingHyperlink ? ((IHighlightingHyperlink) hyperlink).getHighlightingRegion()
								: hyperlink.getHyperlinkRegion());
					}
				}
			}
		}
		return regions;
	}

	public void install(ISourceViewer viewer) {
		this.viewer = viewer;
		((ITextViewerExtension4) viewer).addTextPresentationListener(textPresentationListener);
	}

	public boolean select(IHyperlink hyperlink) {
		return true;
	}

	/**
	 * The hyperlink detector that are used to detect {@link TaskHyperlink task hyperlinks}
	 */
	public void setHyperlinkDetectors(IHyperlinkDetector[] hyperlinkDetectors) {
		this.hyperlinkDetectors = hyperlinkDetectors;
	}

	public void uninstall() {
		((ITextViewerExtension4) viewer).removeTextPresentationListener(textPresentationListener);
		this.viewer = null;
	}

}