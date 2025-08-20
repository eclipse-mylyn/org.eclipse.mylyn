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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextPresentationListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension4;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.mylyn.tasks.ui.IHighlightingHyperlink;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.swt.custom.StyleRange;

/**
 * A manager that ensures that all task hyperlinks have the appropriate text presentation. Subclasses may specify logic for filtering
 * detected hyperlinks and text decoration.
 *
 * @author David Green
 */
public abstract class AbstractHyperlinkTextPresentationManager {

	/**
	 * Regions with a lower offset and a shorter length are ordered before other regions.
	 */
	public static class RegionComparator implements Comparator<IRegion> {

		@Override
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
			} else if (o1.getLength() > o2.getLength()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	private class Support implements ITextPresentationListener {
		@Override
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

	private ITextViewer viewer;

	public AbstractHyperlinkTextPresentationManager() {
	}

	private void addRange(List<StyleRange> ranges, int start, int end) {
		// the style range is to be merged with other styles, so only set the bits that are needed
		StyleRange styleRange = new StyleRange(start, end - start, null, null);
		decorate(styleRange);
		ranges.add(styleRange);
	}

	protected StyleRange[] computeStyleRanges(IRegion extent) {
		if (viewer == null || hyperlinkDetectors == null || viewer.getDocument() == null || extent == null) {
			return null;
		}
		List<IRegion> regions = getRegions(extent);
		if (regions != null) {
			// style ranges may be adjacent but must not overlap, and they must be in order
			// of increasing offset.
			List<StyleRange> ranges = new ArrayList<>(regions.size());
			// sort them first to ensure increasing offset
			Collections.sort(regions, REGION_COMPARATOR);
			// now merge overlapping (and adjacent) ranges
			int start = -1;
			int end = -1;
			for (IRegion region : regions) {
				if (start == -1) {
					start = region.getOffset();
					end = region.getOffset() + region.getLength();
				} else if (region.getOffset() >= end) {
					addRange(ranges, start, end);
					start = region.getOffset();
					end = region.getOffset() + region.getLength();
				} else {
					end = region.getOffset() + region.getLength();
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
							regions = new ArrayList<>();
						}
						regions.add(hyperlink instanceof IHighlightingHyperlink i
								? i.getHighlightingRegion()
								: hyperlink.getHyperlinkRegion());
					}
				}
			}
		}
		return regions;
	}

	public void install(ITextViewer viewer) {
		this.viewer = viewer;
		if (viewer instanceof ITextViewerExtension4) {
			((ITextViewerExtension4) viewer).addTextPresentationListener(textPresentationListener);
		}
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
		viewer = null;
	}

}