/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
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
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.swt.custom.StyleRange;

/**
 * A manager that ensures that all task hyperlinks have the appropriate text presentation, for example strikethrough
 * when the task {@link ITask#isCompleted() is completed}.
 * 
 * @author David Green
 */
public class TaskHyperlinkTextPresentationManager {

	private static final RegionComparator REGION_COMPARATOR = new RegionComparator();

	private ISourceViewer viewer;

	private final ITextPresentationListener textPresentationListener = new Support();

	private IHyperlinkDetector hyperlinkDetector;

	/**
	 * The hyperlink detector that is used to detect {@link TaskHyperlink task hyperlinks}
	 */
	public IHyperlinkDetector getHyperlinkDetector() {
		return hyperlinkDetector;
	}

	/**
	 * The hyperlink detector that is used to detect {@link TaskHyperlink task hyperlinks}
	 */
	public void setHyperlinkDetector(IHyperlinkDetector hyperlinkDetector) {
		this.hyperlinkDetector = hyperlinkDetector;
	}

	public void install(ISourceViewer viewer) {
		this.viewer = viewer;
		((ITextViewerExtension4) viewer).addTextPresentationListener(textPresentationListener);
		viewer.invalidateTextPresentation();
	}

	public void uninstall() {
		((ITextViewerExtension4) viewer).removeTextPresentationListener(textPresentationListener);
		this.viewer = null;
	}

	private class Support implements ITextPresentationListener {
		public void applyTextPresentation(TextPresentation textPresentation) {
			System.err.println(textPresentation.getExtent());
			StyleRange[] styleRanges = computeStyleRanges(textPresentation.getExtent());
			if (styleRanges != null && styleRanges.length > 0) {
				textPresentation.mergeStyleRanges(styleRanges);
			}
		}
	}

	private StyleRange[] computeStyleRanges(IRegion extent) {
		if (viewer == null || hyperlinkDetector == null || viewer.getDocument() == null) {
			return null;
		}
		IHyperlink[] hyperlinks = hyperlinkDetector.detectHyperlinks(viewer, extent, true);
		if (hyperlinks != null && hyperlinks.length > 0) {
			List<IRegion> regions = null;

			TaskList taskList = TasksUiPlugin.getTaskList();

			for (IHyperlink hyperlink : hyperlinks) {
				if (hyperlink instanceof TaskHyperlink) {
					TaskHyperlink taskHyperlink = (TaskHyperlink) hyperlink;
					String taskId = taskHyperlink.getTaskId();
					String repositoryUrl = taskHyperlink.getRepository().getRepositoryUrl();

					ITask task = taskList.getTask(repositoryUrl, taskId);
					if (task == null) {
						task = taskList.getTaskByKey(repositoryUrl, taskId);
					}

					if (task != null && task.isCompleted()) {
						if (regions == null) {
							regions = new ArrayList<IRegion>();
						}
						regions.add(hyperlink.getHyperlinkRegion());
					}
				}
			}
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
		}
		return null;
	}

	private void addRange(List<StyleRange> ranges, int start, int end) {
		// the style range is to be merged with other styles, so only set the bits that are needed
		StyleRange styleRange = new StyleRange(start, end - start, null, null);
		// currently only strikethrough is used to indicate completed tasks
		styleRange.strikeout = true;
		ranges.add(styleRange);
	}

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
}
